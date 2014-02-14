%{
#include <stdio.h>
#include <string.h>
#include "yacc.tab.h"

char *strdup(const char *s);
%}


%%
fields                  {return FIELD;};
title                   {return TITLE;};  
buttons                 {return BUTTON;};
(=)                     {return '=';};
\{[ \t\na-z0-9,_]+\}    {return LIST;};
string                  {return STRING;};
float                   {return FLOAT;};
integer                 {return INTEGER;};
;                       {return 0;};


#.*

([a-z0-9]+[a-z0-9_]*)    {yylval.idval = strdup(yytext);return ID; };
[ \t\n]
%%