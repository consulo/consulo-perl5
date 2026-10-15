// <defaultLiveTemplates file="liveTemplates/TemplateToolkit2.xml"> in upstream perl5.lang.tt2.backend.main.xml
package consulo.perl5.tt2.liveTemplates;

import consulo.annotation.component.ExtensionImpl;
import consulo.language.editor.template.LiveTemplateContributor;
import consulo.localize.LocalizeValue;

@ExtensionImpl
public class TemplateToolkit2LiveTemplateContributorRegistration implements LiveTemplateContributor {
  @Override
  public String groupId() {
    return "Template Toolkit 2";
  }

  @Override
  public LocalizeValue groupName() {
    return LocalizeValue.localizeTODO("Template Toolkit 2");
  }

  @Override
  public void contribute(Factory factory) {
    try (Builder builder = factory.newBuilder("Template Toolkit 2.ge", "ge", "$OPENTAG$ GET $VAR$ $CLOSETAG$", LocalizeValue.localizeTODO("GET directive"))) {
      builder.withVariable("VAR", "\"element\"", "", true);
      builder.withVariable("OPENTAG", "tt2OpenMarker()", "", false);
      builder.withVariable("CLOSETAG", "tt2CloseMarker()", "", false);
      builder.withContext(com.perl5.lang.tt2.idea.liveTemplates.TemplateToolkitTemplateContextTypeCommandPositionRegistration.class, true);
    }
    try (Builder builder = factory.newBuilder("Template Toolkit 2.se", "se", "$OPENTAG$ SET $KEY$ = $VALUE$ $CLOSETAG$", LocalizeValue.localizeTODO("SET directive"))) {
      builder.withVariable("KEY", "\"key\"", "", true);
      builder.withVariable("VALUE", "\"value\"", "", true);
      builder.withVariable("OPENTAG", "tt2OpenMarker()", "", false);
      builder.withVariable("CLOSETAG", "tt2CloseMarker()", "", false);
      builder.withContext(com.perl5.lang.tt2.idea.liveTemplates.TemplateToolkitTemplateContextTypeCommandPositionRegistration.class, true);
    }
    try (Builder builder = factory.newBuilder("Template Toolkit 2.ca", "ca", "$OPENTAG$ CALL $VAR$ $CLOSETAG$", LocalizeValue.localizeTODO("CALL directive"))) {
      builder.withVariable("VAR", "\"element\"", "", true);
      builder.withVariable("OPENTAG", "tt2OpenMarker()", "", false);
      builder.withVariable("CLOSETAG", "tt2CloseMarker()", "", false);
      builder.withContext(com.perl5.lang.tt2.idea.liveTemplates.TemplateToolkitTemplateContextTypeCommandPositionRegistration.class, true);
    }
    try (Builder builder = factory.newBuilder("Template Toolkit 2.def", "def", "$OPENTAG$ DEFAULT $KEY$ = $VALUE$ $CLOSETAG$", LocalizeValue.localizeTODO("DEFAULT directive"))) {
      builder.withVariable("KEY", "\"key\"", "", true);
      builder.withVariable("VALUE", "\"value\"", "", true);
      builder.withVariable("OPENTAG", "tt2OpenMarker()", "", false);
      builder.withVariable("CLOSETAG", "tt2CloseMarker()", "", false);
      builder.withContext(com.perl5.lang.tt2.idea.liveTemplates.TemplateToolkitTemplateContextTypeCommandPositionRegistration.class, true);
    }
    try (Builder builder = factory.newBuilder("Template Toolkit 2.ins", "ins", "$OPENTAG$ INSERT $VAR$ $CLOSETAG$", LocalizeValue.localizeTODO("INSERT directive"))) {
      builder.withVariable("VAR", "\"file\"", "", true);
      builder.withVariable("OPENTAG", "tt2OpenMarker()", "", false);
      builder.withVariable("CLOSETAG", "tt2CloseMarker()", "", false);
      builder.withContext(com.perl5.lang.tt2.idea.liveTemplates.TemplateToolkitTemplateContextTypeCommandPositionRegistration.class, true);
    }
    try (Builder builder = factory.newBuilder("Template Toolkit 2.inc", "inc", "$OPENTAG$ INCLUDE $VAR$ $CLOSETAG$", LocalizeValue.localizeTODO("INCLUDE directive"))) {
      builder.withVariable("VAR", "\"fileorblock\"", "", true);
      builder.withVariable("OPENTAG", "tt2OpenMarker()", "", false);
      builder.withVariable("CLOSETAG", "tt2CloseMarker()", "", false);
      builder.withContext(com.perl5.lang.tt2.idea.liveTemplates.TemplateToolkitTemplateContextTypeCommandPositionRegistration.class, true);
    }
    try (Builder builder = factory.newBuilder("Template Toolkit 2.pro", "pro", "$OPENTAG$ PROCESS $VAR$ $CLOSETAG$", LocalizeValue.localizeTODO("PROCESS directive"))) {
      builder.withVariable("VAR", "\"fileorblock\"", "", true);
      builder.withVariable("OPENTAG", "tt2OpenMarker()", "", false);
      builder.withVariable("CLOSETAG", "tt2CloseMarker()", "", false);
      builder.withContext(com.perl5.lang.tt2.idea.liveTemplates.TemplateToolkitTemplateContextTypeCommandPositionRegistration.class, true);
    }
    try (Builder builder = factory.newBuilder("Template Toolkit 2.en", "en", "$OPENTAG$ END $CLOSETAG$", LocalizeValue.localizeTODO("END directive"))) {
      builder.withVariable("OPENTAG", "tt2OpenMarker()", "", false);
      builder.withVariable("CLOSETAG", "tt2CloseMarker()", "", false);
      builder.withContext(com.perl5.lang.tt2.idea.liveTemplates.TemplateToolkitTemplateContextTypeCommandPositionRegistration.class, true);
    }
    try (Builder builder = factory.newBuilder("Template Toolkit 2.us", "us", "$OPENTAG$ USE $VAR$ $CLOSETAG$", LocalizeValue.localizeTODO("USE directive"))) {
      builder.withVariable("VAR", "\"module\"", "", true);
      builder.withVariable("OPENTAG", "tt2OpenMarker()", "", false);
      builder.withVariable("CLOSETAG", "tt2CloseMarker()", "", false);
      builder.withContext(com.perl5.lang.tt2.idea.liveTemplates.TemplateToolkitTemplateContextTypeCommandPositionRegistration.class, true);
    }
    try (Builder builder = factory.newBuilder("Template Toolkit 2.mr", "mr", "$OPENTAG$ MACRO $VAR$ $VALUE$ $CLOSETAG$", LocalizeValue.localizeTODO("MACRO directive"))) {
      builder.withVariable("VAR", "\"name\"", "", true);
      builder.withVariable("VALUE", "\"directive\"", "", true);
      builder.withVariable("OPENTAG", "tt2OpenMarker()", "", false);
      builder.withVariable("CLOSETAG", "tt2CloseMarker()", "", false);
      builder.withContext(com.perl5.lang.tt2.idea.liveTemplates.TemplateToolkitTemplateContextTypeCommandPositionRegistration.class, true);
    }
    try (Builder builder = factory.newBuilder("Template Toolkit 2.th", "th", "$OPENTAG$ THROW $type$ $message$ $CLOSETAG$", LocalizeValue.localizeTODO("THROW directive"))) {
      builder.withVariable("type", "\"type\"", "", true);
      builder.withVariable("message", "\"message\"", "", true);
      builder.withVariable("OPENTAG", "tt2OpenMarker()", "", false);
      builder.withVariable("CLOSETAG", "tt2CloseMarker()", "", false);
      builder.withContext(com.perl5.lang.tt2.idea.liveTemplates.TemplateToolkitTemplateContextTypeCommandPositionRegistration.class, true);
    }
    try (Builder builder = factory.newBuilder("Template Toolkit 2.ne", "ne", "$OPENTAG$ NEXT $CLOSETAG$", LocalizeValue.localizeTODO("NEXT directive"))) {
      builder.withVariable("OPENTAG", "tt2OpenMarker()", "", false);
      builder.withVariable("CLOSETAG", "tt2CloseMarker()", "", false);
      builder.withContext(com.perl5.lang.tt2.idea.liveTemplates.TemplateToolkitTemplateContextTypeCommandPositionRegistration.class, true);
    }
    try (Builder builder = factory.newBuilder("Template Toolkit 2.la", "la", "$OPENTAG$ LAST $CLOSETAG$", LocalizeValue.localizeTODO("LAST directive"))) {
      builder.withVariable("OPENTAG", "tt2OpenMarker()", "", false);
      builder.withVariable("CLOSETAG", "tt2CloseMarker()", "", false);
      builder.withContext(com.perl5.lang.tt2.idea.liveTemplates.TemplateToolkitTemplateContextTypeCommandPositionRegistration.class, true);
    }
    try (Builder builder = factory.newBuilder("Template Toolkit 2.re", "re", "$OPENTAG$ RETURN $CLOSETAG$", LocalizeValue.localizeTODO("RETURN directive"))) {
      builder.withVariable("OPENTAG", "tt2OpenMarker()", "", false);
      builder.withVariable("CLOSETAG", "tt2CloseMarker()", "", false);
      builder.withContext(com.perl5.lang.tt2.idea.liveTemplates.TemplateToolkitTemplateContextTypeCommandPositionRegistration.class, true);
    }
    try (Builder builder = factory.newBuilder("Template Toolkit 2.st", "st", "$OPENTAG$ STOP $CLOSETAG$", LocalizeValue.localizeTODO("STOP directive"))) {
      builder.withVariable("OPENTAG", "tt2OpenMarker()", "", false);
      builder.withVariable("CLOSETAG", "tt2CloseMarker()", "", false);
      builder.withContext(com.perl5.lang.tt2.idea.liveTemplates.TemplateToolkitTemplateContextTypeCommandPositionRegistration.class, true);
    }
    try (Builder builder = factory.newBuilder("Template Toolkit 2.cl", "cl", "$OPENTAG$ CLEAR $CLOSETAG$", LocalizeValue.localizeTODO("CLEAR directive"))) {
      builder.withVariable("OPENTAG", "tt2OpenMarker()", "", false);
      builder.withVariable("CLOSETAG", "tt2CloseMarker()", "", false);
      builder.withContext(com.perl5.lang.tt2.idea.liveTemplates.TemplateToolkitTemplateContextTypeCommandPositionRegistration.class, true);
    }
    try (Builder builder = factory.newBuilder("Template Toolkit 2.me", "me", "$OPENTAG$ META $KEY$ = $VALUE$ $CLOSETAG$", LocalizeValue.localizeTODO("META directive"))) {
      builder.withVariable("KEY", "\"key\"", "", true);
      builder.withVariable("VALUE", "\"value\"", "", true);
      builder.withVariable("OPENTAG", "tt2OpenMarker()", "", false);
      builder.withVariable("CLOSETAG", "tt2CloseMarker()", "", false);
      builder.withContext(com.perl5.lang.tt2.idea.liveTemplates.TemplateToolkitTemplateContextTypeCommandPositionRegistration.class, true);
    }
    try (Builder builder = factory.newBuilder("Template Toolkit 2.ta", "ta", "$OPENTAG$ TAGS $OPENTAG$ $CLOSETAG$ $CLOSETAG$", LocalizeValue.localizeTODO("TAGS directive"))) {
      builder.withVariable("OPENTAG", "tt2OpenMarker()", "", false);
      builder.withVariable("CLOSETAG", "tt2CloseMarker()", "", false);
      builder.withContext(com.perl5.lang.tt2.idea.liveTemplates.TemplateToolkitTemplateContextTypeCommandPositionRegistration.class, true);
    }
    try (Builder builder = factory.newBuilder("Template Toolkit 2.deb", "deb", "$OPENTAG$ DEBUG $VAR$ $CLOSETAG$", LocalizeValue.localizeTODO("DEBUG directive"))) {
      builder.withVariable("VAR", "\"on\"", "", true);
      builder.withVariable("OPENTAG", "tt2OpenMarker()", "", false);
      builder.withVariable("CLOSETAG", "tt2CloseMarker()", "", false);
      builder.withContext(com.perl5.lang.tt2.idea.liveTemplates.TemplateToolkitTemplateContextTypeCommandPositionRegistration.class, true);
    }
    try (Builder builder = factory.newBuilder("Template Toolkit 2.wr", "wr", "$OPENTAG$ WRAPPER $NAME$ $CLOSETAG$\n    $END$\n$OPENTAG$ END $CLOSETAG$", LocalizeValue.localizeTODO("WRAPPER directive"))) {
      builder.withVariable("NAME", "\"section\"", "", true);
      builder.withVariable("CLOSETAG", "tt2CloseMarker()", "", false);
      builder.withVariable("OPENTAG", "tt2OpenMarker()", "", false);
      builder.withContext(com.perl5.lang.tt2.idea.liveTemplates.TemplateToolkitTemplateContextTypeCommandPositionRegistration.class, true);
    }
    try (Builder builder = factory.newBuilder("Template Toolkit 2.bl", "bl", "$OPENTAG$ BLOCK $NAME$ $CLOSETAG$\n    $END$\n$OPENTAG$ END $CLOSETAG$", LocalizeValue.localizeTODO("BLOCK directive"))) {
      builder.withVariable("NAME", "\"name\"", "", true);
      builder.withVariable("CLOSETAG", "tt2CloseMarker()", "", false);
      builder.withVariable("OPENTAG", "tt2OpenMarker()", "", false);
      builder.withContext(com.perl5.lang.tt2.idea.liveTemplates.TemplateToolkitTemplateContextTypeCommandPositionRegistration.class, true);
    }
    try (Builder builder = factory.newBuilder("Template Toolkit 2.if", "if", "$OPENTAG$ IF $NAME$ $CLOSETAG$\n    $END$\n$OPENTAG$ END $CLOSETAG$", LocalizeValue.localizeTODO("IF block directive"))) {
      builder.withVariable("NAME", "\"condition\"", "", true);
      builder.withVariable("CLOSETAG", "tt2CloseMarker()", "", false);
      builder.withVariable("OPENTAG", "tt2OpenMarker()", "", false);
      builder.withContext(com.perl5.lang.tt2.idea.liveTemplates.TemplateToolkitTemplateContextTypeCommandPositionRegistration.class, true);
    }
    try (Builder builder = factory.newBuilder("Template Toolkit 2.un", "un", "$OPENTAG$ UNLESS $NAME$ $CLOSETAG$\n    $END$\n$OPENTAG$ END $CLOSETAG$", LocalizeValue.localizeTODO("UNLESS block directive"))) {
      builder.withVariable("NAME", "\"condition\"", "", true);
      builder.withVariable("CLOSETAG", "tt2CloseMarker()", "", false);
      builder.withVariable("OPENTAG", "tt2OpenMarker()", "", false);
      builder.withContext(com.perl5.lang.tt2.idea.liveTemplates.TemplateToolkitTemplateContextTypeCommandPositionRegistration.class, true);
    }
    try (Builder builder = factory.newBuilder("Template Toolkit 2.sw", "sw", "$OPENTAG$ SWITCH $NAME$ $CLOSETAG$\n    $END$\n$OPENTAG$ END $CLOSETAG$", LocalizeValue.localizeTODO("SWITCH directive"))) {
      builder.withVariable("NAME", "\"value\"", "", true);
      builder.withVariable("CLOSETAG", "tt2CloseMarker()", "", false);
      builder.withVariable("OPENTAG", "tt2OpenMarker()", "", false);
      builder.withContext(com.perl5.lang.tt2.idea.liveTemplates.TemplateToolkitTemplateContextTypeCommandPositionRegistration.class, true);
    }
    try (Builder builder = factory.newBuilder("Template Toolkit 2.fe", "fe", "$OPENTAG$ FOREACH $ITERATOR$ IN $ITERABLE$ $CLOSETAG$\n    $END$\n$OPENTAG$ END $CLOSETAG$", LocalizeValue.localizeTODO("FOREACH directive"))) {
      builder.withVariable("ITERATOR", "\"iterator\"", "", true);
      builder.withVariable("ITERABLE", "\"iterable\"", "", true);
      builder.withVariable("CLOSETAG", "tt2CloseMarker()", "", false);
      builder.withVariable("OPENTAG", "tt2OpenMarker()", "", false);
      builder.withContext(com.perl5.lang.tt2.idea.liveTemplates.TemplateToolkitTemplateContextTypeCommandPositionRegistration.class, true);
    }
    try (Builder builder = factory.newBuilder("Template Toolkit 2.wh", "wh", "$OPENTAG$ WHILE $CONDITION$ $CLOSETAG$\n    $END$\n$OPENTAG$ END $CLOSETAG$", LocalizeValue.localizeTODO("WHILE directive"))) {
      builder.withVariable("CONDITION", "\"condition\"", "", true);
      builder.withVariable("CLOSETAG", "tt2CloseMarker()", "", false);
      builder.withVariable("OPENTAG", "tt2OpenMarker()", "", false);
      builder.withContext(com.perl5.lang.tt2.idea.liveTemplates.TemplateToolkitTemplateContextTypeCommandPositionRegistration.class, true);
    }
    try (Builder builder = factory.newBuilder("Template Toolkit 2.fi", "fi", "$OPENTAG$ FILTER $NAME$ $CLOSETAG$\n    $END$\n$OPENTAG$ END $CLOSETAG$", LocalizeValue.localizeTODO("FILTER directive"))) {
      builder.withVariable("NAME", "tt2FilterName()", "", true);
      builder.withVariable("CLOSETAG", "tt2CloseMarker()", "", false);
      builder.withVariable("OPENTAG", "tt2OpenMarker()", "", false);
      builder.withContext(com.perl5.lang.tt2.idea.liveTemplates.TemplateToolkitTemplateContextTypeCommandPositionRegistration.class, true);
    }
    try (Builder builder = factory.newBuilder("Template Toolkit 2.pe", "pe", "$OPENTAG$ PERL $CLOSETAG$\n    $END$\n$OPENTAG$ END $CLOSETAG$", LocalizeValue.localizeTODO("PERL block"))) {
      builder.withVariable("CLOSETAG", "tt2CloseMarker()", "", false);
      builder.withVariable("OPENTAG", "tt2OpenMarker()", "", false);
      builder.withContext(com.perl5.lang.tt2.idea.liveTemplates.TemplateToolkitTemplateContextTypeCommandPositionRegistration.class, true);
    }
    try (Builder builder = factory.newBuilder("Template Toolkit 2.rp", "rp", "$OPENTAG$ RAWPERL $CLOSETAG$\n    $END$\n$OPENTAG$ END $CLOSETAG$", LocalizeValue.localizeTODO("RAWPERL block"))) {
      builder.withVariable("CLOSETAG", "tt2CloseMarker()", "", false);
      builder.withVariable("OPENTAG", "tt2OpenMarker()", "", false);
      builder.withContext(com.perl5.lang.tt2.idea.liveTemplates.TemplateToolkitTemplateContextTypeCommandPositionRegistration.class, true);
    }
    try (Builder builder = factory.newBuilder("Template Toolkit 2.try", "try", "$OPENTAG$ TRY $CLOSETAG$\n    $END$\n$OPENTAG$ END $CLOSETAG$", LocalizeValue.localizeTODO("TRY block"))) {
      builder.withVariable("CLOSETAG", "tt2CloseMarker()", "", false);
      builder.withVariable("OPENTAG", "tt2OpenMarker()", "", false);
      builder.withContext(com.perl5.lang.tt2.idea.liveTemplates.TemplateToolkitTemplateContextTypeCommandPositionRegistration.class, true);
    }
    try (Builder builder = factory.newBuilder("Template Toolkit 2.eli", "eli", "$OPENTAG$ ELSIF $CONDITION$ $CLOSETAG$", LocalizeValue.localizeTODO("ELSIF block"))) {
      builder.withVariable("CONDITION", "\"condition\"", "", true);
      builder.withVariable("OPENTAG", "tt2OpenMarker()", "", false);
      builder.withVariable("CLOSETAG", "tt2CloseMarker()", "", false);
      builder.withContext(com.perl5.lang.tt2.idea.liveTemplates.TemplateToolkitTemplateContextTypeCommandPositionRegistration.class, false);
      builder.withContext(com.perl5.lang.tt2.idea.liveTemplates.TemplateToolkitTemplateContextTypeCommandPositionElsifRegistration.class, true);
    }
    try (Builder builder = factory.newBuilder("Template Toolkit 2.el", "el", "$OPENTAG$ ELSE $CLOSETAG$", LocalizeValue.localizeTODO("ELSE"))) {
      builder.withVariable("OPENTAG", "tt2OpenMarker()", "", false);
      builder.withVariable("CLOSETAG", "tt2CloseMarker()", "", false);
      builder.withContext(com.perl5.lang.tt2.idea.liveTemplates.TemplateToolkitTemplateContextTypeCommandPositionRegistration.class, false);
      builder.withContext(com.perl5.lang.tt2.idea.liveTemplates.TemplateToolkitTemplateContextTypeCommandPositionElsifRegistration.class, true);
    }
    try (Builder builder = factory.newBuilder("Template Toolkit 2.cas", "cas", "$OPENTAG$ CASE $CONDITION$ $CLOSETAG$", LocalizeValue.localizeTODO("CASE branch"))) {
      builder.withVariable("CONDITION", "\"condition\"", "", true);
      builder.withVariable("OPENTAG", "tt2OpenMarker()", "", false);
      builder.withVariable("CLOSETAG", "tt2CloseMarker()", "", false);
      builder.withContext(com.perl5.lang.tt2.idea.liveTemplates.TemplateToolkitTemplateContextTypeCommandPositionRegistration.class, false);
      builder.withContext(com.perl5.lang.tt2.idea.liveTemplates.TemplateToolkitTemplateContextTypeCommandPositionCaseRegistration.class, true);
    }
    try (Builder builder = factory.newBuilder("Template Toolkit 2.cat", "cat", "$OPENTAG$ CATCH $TYPE$ $CLOSETAG$", LocalizeValue.localizeTODO("CATCH branch"))) {
      builder.withVariable("TYPE", "\"type\"", "", true);
      builder.withVariable("OPENTAG", "tt2OpenMarker()", "", false);
      builder.withVariable("CLOSETAG", "tt2CloseMarker()", "", false);
      builder.withContext(com.perl5.lang.tt2.idea.liveTemplates.TemplateToolkitTemplateContextTypeCommandPositionRegistration.class, false);
      builder.withContext(com.perl5.lang.tt2.idea.liveTemplates.TemplateToolkitTemplateContextTypeCommandPositionElsifRegistration.class, false);
      builder.withContext(com.perl5.lang.tt2.idea.liveTemplates.TemplateToolkitTemplateContextTypeCommandPositionCatchRegistration.class, true);
    }
    try (Builder builder = factory.newBuilder("Template Toolkit 2.fin", "fin", "$OPENTAG$ FINAL $CLOSETAG$", LocalizeValue.localizeTODO("FINAL branch"))) {
      builder.withVariable("OPENTAG", "tt2OpenMarker()", "", false);
      builder.withVariable("CLOSETAG", "tt2CloseMarker()", "", false);
      builder.withContext(com.perl5.lang.tt2.idea.liveTemplates.TemplateToolkitTemplateContextTypeCommandPositionRegistration.class, false);
      builder.withContext(com.perl5.lang.tt2.idea.liveTemplates.TemplateToolkitTemplateContextTypeCommandPositionElsifRegistration.class, false);
      builder.withContext(com.perl5.lang.tt2.idea.liveTemplates.TemplateToolkitTemplateContextTypeCommandPositionCatchRegistration.class, true);
    }
  }
}
