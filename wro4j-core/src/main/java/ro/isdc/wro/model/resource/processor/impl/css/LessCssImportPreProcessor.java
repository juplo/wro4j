package ro.isdc.wro.model.resource.processor.impl.css;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ro.isdc.wro.model.resource.Resource;

import ro.isdc.wro.model.resource.processor.support.LessCssImportInspector;
import ro.isdc.wro.model.resource.processor.support.ProcessingCriteria;
import ro.isdc.wro.model.resource.processor.support.ProcessingType;
import ro.isdc.wro.util.WroUtil;


/**
 * A processor capable of handling <a href="http://lesscss.org/#-importing">LessCss imports</a>
 *
 * @author Alex Objelean
 * @created 4 Mar 2013
 * @since 1.6.3
 */
public class LessCssImportPreProcessor
    extends CssImportPreProcessor {
  private static final Logger LOG = LoggerFactory.getLogger(LessCssImportPreProcessor.class);

  public static final String ALIAS = "lessCssImport";


  /**
   * {@inheritDoc}
   */
  @Override
  protected String doTransform(final String cssContent, final String resourceUri)
      throws IOException {
    final StringBuffer sb = new StringBuffer();
    final List<Resource> imports = new ArrayList<Resource>();
    LessCssImportInspector inspector = new LessCssImportInspector(cssContent);
    final Matcher m = inspector.getMatcher();
    int pos = 0;
     while (m.find()) {
      sb.append(cssContent.substring(pos, m.start()));
      pos = m.end();
      boolean mediaQuery = !m.group(4).isEmpty();
      if (mediaQuery) {
        LOG.debug("Media-Query found: {}", m.group(4));
        sb.append("@media");
        sb.append(m.group(4));
        sb.append("{");
      }
      final Resource importedResource = createImportedResource(resourceUri, inspector.extractImportUrl(m));
      // check if already exist
      if (imports.contains(importedResource)) {
        LOG.debug("[WARN] Duplicate imported resource: {}", importedResource);
      } else {
        imports.add(importedResource);
        onImportDetected(importedResource.getUri());
        List<Resource> foundImports = new ArrayList<Resource>(1);
        foundImports.add(importedResource);
        // for now, minimize always
        // TODO: find a way to get minimize property dynamically.
        sb.append(preProcessorExecutor.processAndMerge(foundImports,
            ProcessingCriteria.create(ProcessingType.IMPORT_ONLY, false)));
      }
      if (mediaQuery)
        sb.append("}");
    }
    sb.append(cssContent.substring(pos, cssContent.length()));
    if (!imports.isEmpty()) {
      LOG.debug("Imported resources found : {}", imports.size());
    }
    LOG.debug("imports: {}", imports);
    LOG.debug("---\n{}\n---",sb);
    return sb.toString();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected List<String> findImports(final String css) {
    return new LessCssImportInspector(css).findImports();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected String removeImportStatements(final String cssContent) {
    return new LessCssImportInspector(cssContent).removeImportStatements();
  }
}
