package io.github.swagger2markup.internal.helper;

import io.github.swagger2markup.adoc.ast.impl.*;
import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.examples.Example;
import io.swagger.v3.oas.models.headers.Header;
import io.swagger.v3.oas.models.links.Link;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.Encoding;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.oas.models.parameters.RequestBody;
import io.swagger.v3.oas.models.responses.ApiResponse;
import org.apache.commons.lang3.StringUtils;
import org.asciidoctor.ast.*;

import java.util.*;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static io.github.swagger2markup.adoc.converter.internal.Delimiters.*;

public class OpenApiHelpers {

    public static final String LABEL_CONTENT = "Content";
    public static final String LABEL_DEFAULT = "Default";
    public static final String LABEL_DEPRECATED = "Deprecated";
    public static final String LABEL_EXAMPLE = "Example";
    public static final String LABEL_EXAMPLES = "Examples";
    public static final String LABEL_EXCLUSIVE_MAXIMUM = "Exclusive Maximum";
    public static final String LABEL_EXCLUSIVE_MINIMUM = "Exclusive Minimum";
    public static final String LABEL_EXTERNAL_VALUE = "External Value";
    public static final String LABEL_FORMAT = "Format";
    public static final String LABEL_MAXIMUM = "Maximum";
    public static final String LABEL_MAX_ITEMS = "Maximum Items";
    public static final String LABEL_MAX_LENGTH = "Maximum Length";
    public static final String LABEL_MAX_PROPERTIES = "Maximum Properties";
    public static final String LABEL_MINIMUM = "Minimum";
    public static final String LABEL_MIN_ITEMS = "Minimum Items";
    public static final String LABEL_MIN_LENGTH = "Minimum Length";
    public static final String LABEL_MIN_PROPERTIES = "Minimum Properties";
    public static final String LABEL_MULTIPLE_OF = "Multiple Of";
    public static final String LABEL_NO_LINKS = "No Links";
    public static final String LABEL_NULLABLE = "Nullable";
    public static final String LABEL_OPERATION = "Operation";
    public static final String LABEL_OPTIONAL = "Optional";
    public static final String LABEL_PARAMETERS = "Parameters";
    public static final String LABEL_READ_ONLY = "Read Only";
    public static final String LABEL_REQUIRED = "Required";
    public static final String LABEL_SERVER = "Server";
    public static final String LABEL_TERMS_OF_SERVICE = "Terms Of Service";
    public static final String LABEL_TITLE = "Title";
    public static final String LABEL_TYPE = "Type";
    public static final String LABEL_UNIQUE_ITEMS = "Unique Items";
    public static final String LABEL_WRITE_ONLY = "Write Only";
    public static final String SECTION_TITLE_COMPONENTS = "Components";
    public static final String SECTION_TITLE_PARAMETERS = "Parameters";
    public static final String SECTION_TITLE_PATHS = "Paths";
    public static final String SECTION_TITLE_SCHEMAS = "Schemas";
    public static final String SECTION_TITLE_SERVERS = "Servers";
    public static final String SECTION_TITLE_OVERVIEW = "Overview";
    public static final String SECTION_TITLE_TAGS = "Tags";
    public static final String TABLE_HEADER_DEFAULT = "Default";
    public static final String TABLE_HEADER_DESCRIPTION = "Description";
    public static final String TABLE_HEADER_HTTP_CODE = "Code";
    public static final String TABLE_HEADER_LINKS = "Links";
    public static final String TABLE_HEADER_NAME = "Name";
    public static final String TABLE_HEADER_POSSIBLE_VALUES = "Possible Values";
    public static final String TABLE_HEADER_SCHEMA = "Schema";
    public static final String TABLE_HEADER_TYPE = "Type";
    public static final String TABLE_HEADER_VARIABLE = "Variable";
    public static final String TABLE_TITLE_HEADERS = "Headers";
    public static final String TABLE_TITLE_PARAMETERS = "Parameters";
    public static final String TABLE_TITLE_PROPERTIES = "Properties";
    public static final String TABLE_TITLE_RESPONSES = "Responses";
    public static final String TABLE_TITLE_SERVER_VARIABLES = "Server Variables";

    public static void appendDescription(StructuralNode node, String description) {
        if (StringUtils.isNotBlank(description)) {
            Block paragraph = new ParagraphBlockImpl(node);
            paragraph.setSource(description);
            node.append(paragraph);
        }
    }

    public static Document generateLinksDocument(StructuralNode parent, java.util.Map<String, Link> links) {
        DocumentImpl linksDocument = new DocumentImpl(parent);
        ParagraphBlockImpl linkParagraph = new ParagraphBlockImpl(linksDocument);
        if (null == links || links.isEmpty()) {
            linkParagraph.setSource(LABEL_NO_LINKS);
        } else {
            StringBuilder sb = new StringBuilder();
            links.forEach((name, link) -> {
                sb.append(name).append(" +").append(LINE_SEPARATOR);
                sb.append(italicUnconstrained(LABEL_OPERATION)).append(' ').append(italicUnconstrained(link.getOperationId())).append(" +").append(LINE_SEPARATOR);
                Map<String, String> parameters = link.getParameters();
                if (null != parameters && !parameters.isEmpty()) {
                    sb.append(italicUnconstrained(LABEL_PARAMETERS)).append(" {").append(" +").append(LINE_SEPARATOR);
                    parameters.forEach((param, value) -> sb.append('"').append(param).append("\": \"").append(value).append('"').append(" +").append(LINE_SEPARATOR));
                    sb.append('}').append(" +").append(LINE_SEPARATOR);
                }
            });
            linkParagraph.setSource(sb.toString());
        }
        linksDocument.append(linkParagraph);
        return linksDocument;
    }

    public static Document generateSchemaDocument(StructuralNode parent, @SuppressWarnings("rawtypes") Schema schema) {
        Document schemaDocument = new DocumentImpl(parent);
        if (null == schema) return schemaDocument;

        appendDescription(schemaDocument, schema.getDescription());

        Map<String, Boolean> schemasBooleanProperties = new HashMap<String, Boolean>() {{
            put(LABEL_DEPRECATED, schema.getDeprecated());
            put(LABEL_NULLABLE, schema.getNullable());
            put(LABEL_READ_ONLY, schema.getReadOnly());
            put(LABEL_WRITE_ONLY, schema.getWriteOnly());
            put(LABEL_UNIQUE_ITEMS, schema.getUniqueItems());
            put(LABEL_EXCLUSIVE_MAXIMUM, schema.getExclusiveMaximum());
            put(LABEL_EXCLUSIVE_MINIMUM, schema.getExclusiveMinimum());
        }};

        Map<String, Object> schemasValueProperties = new HashMap<String, Object>() {{
            put(LABEL_TITLE, schema.getTitle());
            put(LABEL_TYPE, schema.getType());
            put(LABEL_DEFAULT, schema.getDefault());
            put(LABEL_FORMAT, schema.getFormat());
            put(LABEL_MAXIMUM, schema.getMaximum());
            put(LABEL_MINIMUM, schema.getMinimum());
            put(LABEL_MAX_LENGTH, schema.getMaxLength());
            put(LABEL_MIN_LENGTH, schema.getMinLength());
            put(LABEL_MAX_ITEMS, schema.getMaxItems());
            put(LABEL_MIN_ITEMS, schema.getMinItems());
            put(LABEL_MAX_PROPERTIES, schema.getMaxProperties());
            put(LABEL_MIN_PROPERTIES, schema.getMinProperties());
            put(LABEL_MULTIPLE_OF, schema.getMultipleOf());
        }};

        Stream<String> schemaBooleanStream = schemasBooleanProperties.entrySet().stream()
                .filter(e -> null != e.getValue() && e.getValue())
                .map(e -> italicUnconstrained(e.getKey().toLowerCase()));
        Stream<String> schemaValueStream = schemasValueProperties.entrySet().stream()
                .filter(e -> null != e.getValue() && StringUtils.isNotBlank(e.getValue().toString()))
                .map(e -> e.getKey().toLowerCase() + ": " + e.getValue());

        ParagraphBlockImpl paragraphBlock = new ParagraphBlockImpl(schemaDocument);
        String source = Stream.concat(schemaBooleanStream, schemaValueStream).collect(Collectors.joining(" +" + LINE_SEPARATOR));
        source = generateRefLink(source, schema.get$ref(), "");
        paragraphBlock.setSource(source);

        schemaDocument.append(paragraphBlock);
        appendPropertiesTable(schemaDocument, schema.getProperties(), schema.getRequired());
        return schemaDocument;
    }

    private static String generateRefLink(String source, String ref, String alt) {
        if (StringUtils.isNotBlank(ref)) {
            if (StringUtils.isBlank(alt)) {
                alt = ref.substring(ref.lastIndexOf('/') + 1);
            }
            String anchor = ref.replaceFirst("#", "").replaceAll("/", "_");
            source += "<<" + anchor + "," + alt + ">>" + LINE_SEPARATOR;
        }
        return source;
    }

    public static void appendPropertiesTable(StructuralNode parent, @SuppressWarnings("rawtypes") Map<String, Schema> properties, List<String> schemaRequired) {
        if (null == properties || properties.isEmpty()) return;

        List<String> finalSchemaRequired = (null == schemaRequired) ? new ArrayList<>() : schemaRequired;

        TableImpl propertiesTable = new TableImpl(parent, new HashMap<>(), new ArrayList<>());
        propertiesTable.setOption("header");
        propertiesTable.setAttribute("caption", "", true);
        propertiesTable.setAttribute("cols", ".^4a,.^16a", true);
        propertiesTable.setTitle(TABLE_TITLE_PROPERTIES);
        propertiesTable.setHeaderRow(TABLE_HEADER_NAME, TABLE_HEADER_SCHEMA);

        properties.forEach((name, schema) -> propertiesTable.addRow(
                generateInnerDoc(propertiesTable, name + LINE_SEPARATOR + requiredIndicator(finalSchemaRequired.contains(name))),
                generateSchemaDocument(propertiesTable, schema)
        ));
        parent.append(propertiesTable);
    }

    public static void appendHeadersTable(StructuralNode node, Map<String, Header> headers) {
        if (null == headers || headers.isEmpty()) return;

        TableImpl responseHeadersTable = new TableImpl(node, new HashMap<>(), new ArrayList<>());
        responseHeadersTable.setOption("header");
        responseHeadersTable.setAttribute("caption", "", true);
        responseHeadersTable.setAttribute("cols", ".^2a,.^14a,.^4a", true);
        responseHeadersTable.setTitle(TABLE_TITLE_HEADERS);
        responseHeadersTable.setHeaderRow(TABLE_HEADER_NAME, TABLE_HEADER_DESCRIPTION, TABLE_HEADER_SCHEMA);
        headers.forEach((name, header) ->
                responseHeadersTable.addRow(
                        generateInnerDoc(responseHeadersTable, name),
                        generateInnerDoc(responseHeadersTable, Optional.ofNullable(header.getDescription()).orElse("")),
                        generateSchemaDocument(responseHeadersTable, header.getSchema())
                ));
        node.append(responseHeadersTable);
    }

    static void appendParameters(StructuralNode parent, Map<String, Parameter> parameters) {
        if (null == parameters || parameters.isEmpty()) return;

        TableImpl pathParametersTable = new TableImpl(parent, new HashMap<>(), new ArrayList<>());
        pathParametersTable.setOption("header");
        pathParametersTable.setAttribute("caption", "", true);
        pathParametersTable.setAttribute("cols", ".^2a,.^3a,.^10a,.^5a", true);
        pathParametersTable.setTitle(TABLE_TITLE_PARAMETERS);
        pathParametersTable.setHeaderRow(TABLE_HEADER_TYPE, TABLE_HEADER_NAME, TABLE_HEADER_DESCRIPTION, TABLE_HEADER_SCHEMA);

        parameters.forEach((alt, parameter) ->
                pathParametersTable.addRow(
                        generateInnerDoc(pathParametersTable, boldUnconstrained(parameter.getIn()), alt),
                        getParameterNameDocument(pathParametersTable, parameter),
                        generateInnerDoc(pathParametersTable, Optional.ofNullable(parameter.getDescription()).orElse("")),
                        generateSchemaDocument(pathParametersTable, parameter.getSchema())
                ));
        parent.append(pathParametersTable);
    }

    static void appendParameters(StructuralNode node, List<Parameter> parameters) {
        if (null == parameters || parameters.isEmpty()) return;

        appendParameters(node, parameters.stream().collect(Collectors.toMap(Parameter::getName, parameter -> parameter)));
    }

    static void appendRequestBody(StructuralNode node, RequestBody requestBody) {
        if (null == requestBody) return;
    }

    static void appendResponses(StructuralNode serverSection, Map<String, ApiResponse> apiResponses) {
        if (null == apiResponses || apiResponses.isEmpty()) return;
        TableImpl pathResponsesTable = new TableImpl(serverSection, new HashMap<>(), new ArrayList<>());
        pathResponsesTable.setOption("header");
        pathResponsesTable.setAttribute("caption", "", true);
        pathResponsesTable.setAttribute("cols", ".^2a,.^14a,.^4a", true);
        pathResponsesTable.setTitle(TABLE_TITLE_RESPONSES);
        pathResponsesTable.setHeaderRow(TABLE_HEADER_HTTP_CODE, TABLE_HEADER_DESCRIPTION, TABLE_HEADER_LINKS);

        apiResponses.forEach((httpCode, apiResponse) -> pathResponsesTable.addRow(
                generateInnerDoc(pathResponsesTable, httpCode),
                getResponseDescriptionColumnDocument(pathResponsesTable, apiResponse),
                generateLinksDocument(pathResponsesTable, apiResponse.getLinks())
        ));
        serverSection.append(pathResponsesTable);
    }

    static Document getResponseDescriptionColumnDocument(Table table, ApiResponse apiResponse) {
        Document document = generateInnerDoc(table, Optional.ofNullable(apiResponse.getDescription()).orElse(""));
        appendHeadersTable(document, apiResponse.getHeaders());
        appendMediaContent(document, apiResponse.getContent());
        return document;
    }

    static void appendMediaContent(StructuralNode node, Content content) {
        if (content == null || content.isEmpty()) return;

        DescriptionListImpl mediaContentList = new DescriptionListImpl(node);
        mediaContentList.setTitle(LABEL_CONTENT);

        content.forEach((type, mediaType) -> {
            DescriptionListEntryImpl tagEntry = new DescriptionListEntryImpl(mediaContentList, Collections.singletonList(new ListItemImpl(mediaContentList, type)));
            ListItemImpl tagDesc = new ListItemImpl(tagEntry, "");

            Document document = generateSchemaDocument(mediaContentList, mediaType.getSchema());
            appendMediaTypeExample(document, mediaType.getExample());
            appendExamples(document, mediaType.getExamples());
            appendEncoding(document, mediaType.getEncoding());
            tagDesc.append(document);

            tagEntry.setDescription(tagDesc);
            mediaContentList.addEntry(tagEntry);
        });
        node.append(mediaContentList);
    }

    private static void appendEncoding(StructuralNode node, Map<String, Encoding> encodings) {
        if (encodings == null || encodings.isEmpty()) return;

        DescriptionListImpl encodingList = new DescriptionListImpl(node);
        encodingList.setTitle(LABEL_EXAMPLES);

        encodings.forEach((name, encoding) -> {
            DescriptionListEntryImpl encodingEntry = new DescriptionListEntryImpl(encodingList, Collections.singletonList(new ListItemImpl(encodingList, name)));
            ListItemImpl tagDesc = new ListItemImpl(encodingEntry, "");
            ParagraphBlockImpl encodingBlock = new ParagraphBlockImpl(tagDesc);

            StringBuilder sb = new StringBuilder();
            String contentType = encoding.getContentType();
            if(StringUtils.isNotBlank(contentType)){
                sb.append("Content-Type:").append(contentType).append(LINE_SEPARATOR);
            }
            if(encoding.getAllowReserved()){
                sb.append(italicUnconstrained("Allow Reserved").toLowerCase()).append(LINE_SEPARATOR);
            }
            if(encoding.getExplode()){
                sb.append(italicUnconstrained("Explode").toLowerCase()).append(LINE_SEPARATOR);
            }
            Encoding.StyleEnum style = encoding.getStyle();
            if(style != null){
                sb.append("style").append(style).append(LINE_SEPARATOR);
            }
            encodingBlock.setSource(sb.toString());
            tagDesc.append(encodingBlock);
            appendHeadersTable(tagDesc, encoding.getHeaders());

            encodingEntry.setDescription(tagDesc);

            encodingList.addEntry(encodingEntry);
        });
        node.append(encodingList);
    }

    static void appendExamples(StructuralNode node, Map<String, Example> examples) {
        if (examples == null || examples.isEmpty()) return;

        DescriptionListImpl examplesList = new DescriptionListImpl(node);
        examplesList.setTitle(LABEL_EXAMPLES);

        examples.forEach((name, example) -> {
            DescriptionListEntryImpl exampleEntry = new DescriptionListEntryImpl(examplesList, Collections.singletonList(new ListItemImpl(examplesList, name)));
            ListItemImpl tagDesc = new ListItemImpl(exampleEntry, "");

            ParagraphBlockImpl exampleBlock = new ParagraphBlockImpl(tagDesc);

            appendDescription(exampleBlock, example.getSummary());
            appendDescription(exampleBlock, example.getDescription());
            appendMediaTypeExample(tagDesc, example.getValue());

            ParagraphBlockImpl paragraphBlock = new ParagraphBlockImpl(tagDesc);
            String source = "";
            generateRefLink(source, example.getExternalValue(), LABEL_EXTERNAL_VALUE);
            generateRefLink(source, example.get$ref(), "");
            if(StringUtils.isNotBlank(source)){
                paragraphBlock.setSource(source);
                tagDesc.append(paragraphBlock);
            }

            exampleEntry.setDescription(tagDesc);

            examplesList.addEntry(exampleEntry);
        });
        node.append(examplesList);
    }

    static void appendMediaTypeExample(StructuralNode node, Object example) {
        if (example == null || StringUtils.isBlank(example.toString())) return;

        ParagraphBlockImpl sourceBlock = new ParagraphBlockImpl(node);
        sourceBlock.setTitle(LABEL_EXAMPLE);
        sourceBlock.setAttribute("style", "source", true);
        sourceBlock.setSource(DELIMITER_BLOCK + LINE_SEPARATOR + example + LINE_SEPARATOR + DELIMITER_BLOCK);
        node.append(sourceBlock);
    }

    private static Document getParameterNameDocument(Table table, Parameter parameter) {
        String documentContent = boldUnconstrained(parameter.getName()) + " +" + LINE_SEPARATOR + requiredIndicator(parameter.getRequired());
        return generateInnerDoc(table, documentContent);
    }

    public static Document generateInnerDoc(Table table, String documentContent) {
        return generateInnerDoc(table, documentContent, "");
    }

    public static Document generateInnerDoc(Table table, String documentContent, String id) {
        Document innerDoc = new DocumentImpl(table);
        if (StringUtils.isNotBlank(id)) {
            innerDoc.setId(id);
        }

        Block paragraph = new ParagraphBlockImpl(innerDoc);
        paragraph.setSource(documentContent);
        innerDoc.append(paragraph);
        return innerDoc;
    }

    public static String requiredIndicator(boolean isRequired) {
        return italicUnconstrained(isRequired ? LABEL_REQUIRED : LABEL_OPTIONAL).toLowerCase();
    }

    public static void appendExternalDoc(StructuralNode node, ExternalDocumentation extDoc) {
        if (extDoc == null) return;

        String url = extDoc.getUrl();
        if (StringUtils.isNotBlank(url)) {
            Block paragraph = new ParagraphBlockImpl(node);
            String desc = extDoc.getDescription();
            paragraph.setSource(url + (StringUtils.isNotBlank(desc) ? "[" + desc + "]" : ""));
            node.append(paragraph);
        }
    }

    public static String superScript(String str) {
        return "^" + str + "^";
    }

    public static String subScript(String str) {
        return "~" + str + "~";
    }

    public static String italicUnconstrained(String str) {
        return "__" + str + "__";
    }

    public static String boldUnconstrained(String str) {
        return "**" + str + "**";
    }

    public static String monospaced(String str) {
        return "`" + str + "`";
    }
}
