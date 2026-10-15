/*
 * Copyright 2015-2026 Alexandr Evstigneev
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.perl5.lang.perl.idea.intellilang;

import com.perl5.lang.perl.PerlLanguage;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public final class PerlDefaultInjectionMarkers {
  public static final PerlDefaultInjectionMarkers INSTANCE = new PerlDefaultInjectionMarkers();

  public static final String PERL5_MARKER = "PERL5";

  public static final Map<String, String> DEFAULT_MARKERS;

  static {
    Map<String, String> map = new LinkedHashMap<>();
    map.put("ACTIONSCRIPT", "ECMA Script Level 4");
    map.put("ADB", "AndroidDataBinding");
    map.put("AGSL", "AGSL");
    map.put("AIDL", "AIDL");
    map.put("ANGULAR", "AngularJS");
    map.put("ANGULAR2", "ANGULAR2");
    map.put("APACHE_CONFIG", "ApacheConfig");
    map.put("APPLEJS", "Apple JS");
    map.put("ASP", "Asp");
    map.put("ASPECT_JS", "AspectJS");
    map.put("ASTRO", "Astro");
    map.put("AZURE", "AZURE");
    map.put("BIGQUERY", "BigQuery");
    map.put("BLADE", "Blade");
    map.put("CASSANDRA", "CassandraQL");
    map.put("CFML", "CFML");
    map.put("CGO", "cgo");
    map.put("CHAMELEON", "Chameleon");
    map.put("CLICKHOUSE", "ClickHouse");
    map.put("CMAKE", "CMake");
    map.put("CMAKE_CACHE", "CMakeCache");
    map.put("COCKROACH", "Cockroach");
    map.put("COFFESCRIPT", "COFFEESCRIPT");
    map.put("COMMAND_LINE", "CommandLine");
    map.put("COMPOSER_LOG", "Composer Log");
    map.put("CONCEPT", "Concept");
    map.put("COOKIE", "Cookie");
    map.put("COUCHBASE", "CouchbaseQuery");
    map.put("CSS", "CSS");
    map.put("CSS_VALUE", "CSS VALUE");
    map.put("CYTHON", "Cython");
    map.put("DART", "Dart");
    map.put("DART_IN_HTML", "Dart in Html");
    map.put("DATALORE", "Datalore");
    map.put("DATALORE_PYTHON", "DatalorePython");
    map.put("DB2", "DB2");
    map.put("DB2IS", "DB2_IS");
    map.put("DB2ZOS", "DB2_ZOS");
    map.put("DERBY", "Derby");
    map.put("DEVICE_SPEC", "DeviceSpec");
    map.put("DJANGO", "Django");
    map.put("DOCKERFILE", "Dockerfile");
    map.put("DOXYGEN", "Doxygen");
    map.put("DQL", "DQL");
    map.put("DROOLS", "Drools");
    map.put("DTD", "DTD");
    map.put("ECMA6", "ECMAScript 6");
    map.put("EDITOR_CONFIG", "EditorConfig");
    map.put("EJS", "EJS");
    map.put("EL", "EL");
    map.put("EPP", "Embedded Puppet");
    map.put("ERB", "RHTML");
    map.put("EXASOL", "Exasol");
    map.put("FACELETS", "Facelets");
    map.put("FLOWJS", "Flow JS");
    map.put("FREEMARKER", "FTL");
    map.put("GDB", "GDB");
    map.put("GHERKIN", "Gherkin");
    map.put("GO", "go");
    map.put("GOBUILD", "GoBuild");
    map.put("GOFUZZ", "GoFuzzCorpus");
    map.put("GOTPL", "GoTemplate");
    map.put("GQL", "GQL");
    map.put("GREENPLUM", "Greenplum");
    map.put("GROOVY", "Groovy");
    map.put("GSP", "GSP");
    map.put("GSQL", "GenericSQL");
    map.put("GWT", "GWT JavaScript");
    map.put("GWTCSS", "GWT-CSS");
    map.put("H2", "H2");
    map.put("HAML", "Haml");
    map.put("HBL", "Handlebars");
    map.put("HCL", "HCL");
    map.put("HEXDUMP", "HEXDUMP");
    map.put("HIL", "HIL");
    map.put("HIVEQL", "HiveQL");
    map.put("HJSON", "HelmJSON");
    map.put("HSQLDB", "HSQLDB");
    map.put("HTEXT", "HelmTEXT");
    map.put("HTML", "HTML");
    map.put("HTTP", "HTTP Request");
    map.put("HYAML", "HelmYAML");
    map.put("IGNORE", "IgnoreLang");
    map.put("INI", "Ini");
    map.put("JADE", "Jade");
    map.put("JAVA", "JAVA");
    map.put("JINJA2", "Jinja2");
    map.put("JKTMETA", "JKTMeta");
    map.put("JPAQL", "JPAQL");
    map.put("JQL", "JQL");
    map.put("JQUERYCSS", "JQuery-CSS");
    map.put("JS", "JavaScript");
    map.put("JS15", "JavaScript 1.5");
    map.put("JS16", "JavaScript 1.6");
    map.put("JS17", "JavaScript 1.7");
    map.put("JS18", "JavaScript 1.8");
    map.put("JSHELL", "JShellLanguage");
    map.put("JSON", "JSON");
    map.put("JSONPATH", "JSONPath");
    map.put("JSP", "JSP");
    map.put("JSPX", "JSPX");
    map.put("JSX", "JSX Harmony");
    map.put("JUPYTERPYTHON", "JupyterPython");
    map.put("JYPYTER", "Jupyter");
    map.put("LESS", "LESS");
    map.put("LINKER", "LinkerScript");
    map.put("LIQUID", "liquid");
    map.put("LLDB", "LLDB");
    map.put("LOCALE", "Locale");
    map.put("LOGCAT", "LogcatFilter");
    map.put("LOMBOK", "Lombok.Config");
    map.put("MAKEFILE", "Makefile");
    map.put("MANIFEST", "Manifest");
    map.put("MARIA", "MariaDB");
    map.put("MARKDOWN", "Markdown");
    map.put("MDX", "MDX");
    map.put("MDXJS", "MdxJS");
    map.put("METAJSON", "Metadata JSON");
    map.put("MONGO", "MongoDB");
    map.put("MONGOJS", "MongoJS");
    map.put("MONGOJSON", "MongoDB-JSON");
    map.put("MXML", "Mxml");
    map.put("MYSQL", "MySQL");
    map.put("NASHORNJS", "Nashorn JS");
    map.put("OBJECTIVEC", "ObjectiveC");
    map.put("OGNL", "OGNL");
    map.put("OSQL", "Oracle");
    map.put("OSQLP", "OracleSqlPlus");
    map.put("PBX", "pbx");
    map.put(PERL5_MARKER, PerlLanguage.INSTANCE.getID());
    map.put("PGSQL", "PostgreSQL");
    map.put("PHP", "InjectablePHP");
    map.put("PHPT", "PHPT");
    map.put("PLAN9", "plan9_x86");
    map.put("PLAY", "Play");
    map.put("PLIST", "Plist");
    map.put("POSTCSS", "PostCSS");
    map.put("PRISMA", "Prisma");
    map.put("PROPERTIES", "Properties");
    map.put("PROTOBUF", "protobuf");
    map.put("PUPPET", "Puppet");
    map.put("PYTHON", "Python");
    map.put("QML", "QML");
    map.put("QUARTO", "Quarto");
    map.put("QUTE", "Qute");
    map.put("R", "R");
    map.put("RBS", "ruby.rbs");
    map.put("RDOC", "RDOC");
    map.put("REDIS", "Redis");
    map.put("REDSHIFT", "Redshift");
    map.put("REGEXP", "RegExp");
    map.put("RELAXNG", "RELAX-NG");
    map.put("RENDERSCRIPT", "Renderscript");
    map.put("REST", "ReST");
    map.put("RMARKDOWN", "RMarkdown");
    map.put("ROOMSQL", "RoomSql");
    map.put("ROXYGEN", "Roxygen");
    map.put("RUBY", "ruby");
    map.put("SASS", "SASS");
    map.put("SCSS", "SCSS");
    map.put("SHELL", "Shell Script");
    map.put("SJS", "SvelteJS");
    map.put("SLIM", "Slim");
    map.put("SMALI", "Smali");
    map.put("SMARTY", "Smarty");
    map.put("SMARTYCONFIG", "SmartyConfig");
    map.put("SNOWFLAKE", "Snowflake");
    map.put("SPACEBARS", "Spacebars");
    map.put("SPARKSQL", "SparkSQL");
    map.put("SPEC", "Specification");
    map.put("SPEL", "SpEL");
    map.put("SPI", "SPI");
    map.put("SQL", "SQL");
    map.put("SQL92", "SQL92");
    map.put("SQLITE", "SQLite");
    map.put("STS", "SvelteTS");
    map.put("STYLUS", "Stylus");
    map.put("SVG", "SVG");
    map.put("SWIFT", "Swift");
    map.put("SYBASE", "Sybase");
    map.put("TERRAFORM", "Terraform Config");
    map.put("TEXT", "TEXT");
    map.put("TEXTMATE", "textmate");
    map.put("TOML", "TOML");
    map.put("TS", "TypeScript");
    map.put("TSJSX", "TypeScript JSX");
    map.put("TSQL", "TSQL");
    map.put("TWIG", "Twig");
    map.put("VCPKG", "VCPKG");
    map.put("VERTICA", "Vertica");
    map.put("VGO", "vgo");
    map.put("VJS", "VueJS");
    map.put("VTL", "VTL");
    map.put("VTS", "VueTS");
    map.put("WERF", "WerfYAML");
    map.put("XHTML", "XHTML");
    map.put("XIB", "XIB");
    map.put("XML", "XML");
    map.put("XPATH", "XPath");
    map.put("XPATH2", "XPath2");
    map.put("XSLT", "$XSLT");
    map.put("YAML", "yaml");
    map.put("YOUTRACK", "YouTrack");
    map.put("ZEPPELIN", "Zeppelin");
    DEFAULT_MARKERS = Collections.unmodifiableMap(map);
  }

  private PerlDefaultInjectionMarkers() {
  }
}
