package com.perl5.lang.pod.lexer;

/*
    http://jflex.de/manual.html
    http://www2.cs.tum.edu/projects/cup

*/

import com.intellij.lexer.FlexLexer;
import com.intellij.psi.TokenType;
import com.intellij.psi.tree.IElementType;
import com.perl5.lang.pod.lexer.PodElementTypes;
import org.jetbrains.annotations.NotNull;

%%

%class PodLexer
%extends PodLexerProto
%implements FlexLexer, PodElementTypes
%unicode
%public

%function advance
%type IElementType

%{
	private int lastCodeLineEnd = 0;
    private int codeBlockStart = 0;

    protected void beginCodeBlock()
    {
        yybegin(LEX_CODE);
		codeBlockStart = zzStartRead;
        markLastCodeLine();
    }

	protected void markLastCodeLine()
	{
        lastCodeLineEnd = zzMarkedPos;
	}

	protected IElementType endCodeBlock()
    {
        // return to the last real code line
        zzCurrentPos = zzStartRead = codeBlockStart;
        yypushback(zzMarkedPos - lastCodeLineEnd );
        yybegin(YYINITIAL);
        return POD_CODE;
    }
%}

LINE_TERMINATOR = \r?\n
WHITE_SPACE     = [ \t\f]+

TEXT_ANY        = .*
CODE_LINE       = {WHITE_SPACE}[^\ \t\f\n\r].*{LINE_TERMINATOR}
EMPTY_LINE      = {WHITE_SPACE}*{LINE_TERMINATOR}

POD_TAG         = \=(pod|head1|head2|head3|head4|over|item|back|begin|end|for|encoding)
POD_CLOSE       = \="cut"

%xstate LEX_POD_LINE, YYINITIAL, LEX_LAST_LINE, LEX_TAG_LINE, LEX_BAD_LINE, LEX_CODE
%%

<YYINITIAL>{
    {POD_TAG}  {yybegin(LEX_TAG_LINE);return POD_TAG;}
    {POD_CLOSE} {yybegin(LEX_LAST_LINE); return POD_TAG;}
    {EMPTY_LINE} { return POD_NEWLINE;}
    {CODE_LINE} {beginCodeBlock(); break;}
    .   {yypushback(1); yybegin(LEX_POD_LINE); break;}
}

<LEX_POD_LINE>{
    {TEXT_ANY} {return POD_TEXT;}
    {EMPTY_LINE} {yybegin(YYINITIAL); return POD_NEWLINE;}
}


<LEX_CODE>
{
    {EMPTY_LINE} {break;}
    {CODE_LINE} {markLastCodeLine(); break;}
    .   {return endCodeBlock();}
}

<LEX_TAG_LINE>{
    .* {return POD_TEXT;}
    {EMPTY_LINE} {yybegin(YYINITIAL); return POD_NEWLINE;}
}

<LEX_LAST_LINE>{
    {TEXT_ANY}  { return POD_TEXT;}
    {EMPTY_LINE} {yybegin(YYINITIAL); return POD_NEWLINE;}
}
