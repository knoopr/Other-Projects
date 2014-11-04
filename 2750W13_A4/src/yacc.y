%{
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <sys/types.h>
#include <sys/stat.h>
#include "yacc.tab.h"
#include "ParameterManager.h"
    
    extern int yylex();
    extern int yyparse();
    extern FILE *yyin;
    
    int yyerror(const char * s);
    /*void Manage_list(char * the_List, Boolean is_Fields);
     void Add_item(char* the_ID, char* the_Value);*/
    char id_String[100];
    char set_Fields[2000];
    char get_Fields[2000];
    char add_Fields[2000];
    char add_Buttons[2000];
    char set_Buttons[2000];
    char getset_Interface[2000];
    char getset_Methods[5000];
    char temp_String[1000];
    char temp_String2[1000];
    char project_Title[100];
    char mysql_Entry[1000];
    char boolean_Fields[1000];
    char action_ADD[1000];
    char action_QUERY[1000];
    char action_DELETE[2000];
    char action_UPDATE[1000];
    char confirm_Delete[500];
    
    int entry_Count = 0, number_Field = 0, button_Error = 0;
    %}

%union {
    char *idval;
}

%token FIELD
%token BUTTON
%token TITLE
%token STRING
%token INTEGER
%token FLOAT
%token <idval> ID
%token LIST
%destructor { free($$); } ID

%%

needed:
TITLE '=' name {strcpy(project_Title, id_String); entry_Count++;}
|FIELD '=' LIST {entry_Count++;}
|BUTTON '=' LIST {entry_Count++;}
|name '=' STRING    {
    entry_Count++;
    number_Field++;
    
    sprintf(temp_String,"public JTextField %s = new JTextField();\n\t", id_String);
    strcat(set_Fields,temp_String);
    
    sprintf(temp_String,"Add_field(\"%s\" , %s );\n\t", id_String, id_String);
    strcat(add_Fields,temp_String);
    
    sprintf(temp_String,"\n\tpublic String getDC%s() throws IllegalFieldValueException;\n\tpublic void setDC%s(String the_Text);",id_String,id_String);
    strcat(getset_Interface,temp_String);
    
    sprintf(temp_String,"public String getDC%s() throws IllegalFieldValueException {\n\t\tif(%s.getText().equals(\"\"))\n\t\t\tthrow new IllegalFieldValueException(\"%s is empty\\n\");\n\t\treturn %s.getText();\n\t}\n\tpublic void setDC%s(String the_Text){\n\t\t%s.setText(the_Text);\n\t}\n\t", id_String, id_String, id_String, id_String, id_String, id_String);
    strcat(getset_Methods, temp_String);
    
    strcat(mysql_Entry, id_String);
    strcat(mysql_Entry, " CHAR(100),");
    
    strcat(boolean_Fields, "check_Table(\"");
    strcat(boolean_Fields, id_String);
    strcat(boolean_Fields, "\" , \"CHAR\") && ");
    
    strcat(action_ADD, "+ getDC");
    strcat(action_ADD, id_String);
    strcat(action_ADD, "() + \"', '\"");
    
    sprintf(temp_String, "\n\t\ttry{\n\t\t\tfind_String = find_String + \"%s='\" + getDC%s() + \"' AND \";\n\t\t}catch (Exception e){\n\t\t    if (e.getLocalizedMessage().contains(\"illegal\"))\n\t\t\tthrow new Exception(e.getLocalizedMessage());}", id_String,id_String,id_String);
    strcat(action_DELETE, temp_String);
    
    sprintf(temp_String,"\"%s = \" + rs.getString(\"%s\") +  \",   \" + ", id_String, id_String);
    strcat(confirm_Delete, temp_String);
    
    sprintf(temp_String,"setDC%s(rs.getString(\"%s\"));\n\t\t\t", id_String, id_String);
    strcat(action_QUERY, temp_String);
    
    sprintf(temp_String, "\n\t\ttry{\n\t\t\treplace_String = replace_String + \"%s='\" + getDC%s() + \"', \";\n\t\t}catch (Exception e){}", id_String,id_String,id_String);
    strcat(action_UPDATE, temp_String)
}


|name '=' INTEGER   {
    entry_Count++;
    number_Field++;
    
    sprintf(temp_String,"public JTextField %s = new JTextField();\n\t", id_String);
    strcat(set_Fields,temp_String);
    
    sprintf(temp_String,"Add_field(\"%s\" , %s );\n\t", id_String, id_String);
    strcat(add_Fields,temp_String);
    
    sprintf(temp_String,"\n\tpublic String getDC%s() throws IllegalFieldValueException;\n\tpublic void setDC%s(String the_Text);",id_String,id_String);
    strcat(getset_Interface,temp_String);
    
    sprintf(temp_String,"public String getDC%s() throws IllegalFieldValueException {\n\t\tif(%s.getText().equals(\"\"))\n\t\t\tthrow new IllegalFieldValueException(\"%s is empty\\n\");\n\t\ttry {\n\t\t\tInteger.parseInt(%s.getText());\n\t\t}catch (Exception e) {\n\t\t\tthrow new IllegalFieldValueException(\"%s contains the illegal value: \" + %s.getText() + \" it should contain an integer\"); \n\t\t}\n\t\treturn %s.getText();\n\t}\n\tpublic void setDC%s(String the_Text){\n\t\t%s.setText(the_Text);\n\t}\n\t", id_String, id_String, id_String, id_String, id_String, id_String, id_String, id_String, id_String);
    strcat(getset_Methods, temp_String);
    
    strcat(mysql_Entry, id_String);
    strcat(mysql_Entry, " INT,");
    
    strcat(boolean_Fields, "check_Table(\"");
    strcat(boolean_Fields, id_String);
    strcat(boolean_Fields, "\" , \"INT\") && ");
    
    strcat(action_ADD, "+ getDC");
    strcat(action_ADD, id_String);
    strcat(action_ADD, "() + \"', '\"");
    
    sprintf(temp_String, "\n\t\ttry{\n\t\t\tfind_String = find_String + \"%s='\" + getDC%s() + \"' AND \";\n\t\t}catch (Exception e){\n\t\t    if (e.getLocalizedMessage().contains(\"illegal\"))\n\t\t\tthrow new Exception(e.getLocalizedMessage());}", id_String,id_String,id_String);
    strcat(action_DELETE, temp_String);
    
    sprintf(temp_String,"\"%s = \" + rs.getString(\"%s\") +  \",   \" + ", id_String, id_String);
    strcat(confirm_Delete, temp_String);
    
    sprintf(temp_String,"setDC%s(rs.getString(\"%s\"));\n\t\t\t", id_String, id_String);
    strcat(action_QUERY, temp_String);
    
    sprintf(temp_String, "\n\t\ttry{\n\t\t\treplace_String = replace_String + \"%s='\" + getDC%s() + \"', \";\n\t\t}catch (Exception e){\n\t\t    if (e.getLocalizedMessage().contains(\"illegal\"))\n\t\t\tthrow new Exception(e.getLocalizedMessage());}", id_String,id_String,id_String);
    strcat(action_UPDATE, temp_String)
}


|name '=' FLOAT     {
    entry_Count++;
    number_Field++;
    
    sprintf(temp_String,"public JTextField %s = new JTextField();\n\t", id_String);
    strcat(set_Fields,temp_String);
    
    sprintf(temp_String,"Add_field(\"%s\" , %s );\n\t", id_String, id_String);
    strcat(add_Fields,temp_String);
    
    sprintf(temp_String,"\n\tpublic String getDC%s() throws IllegalFieldValueException;\n\tpublic void setDC%s(String the_Text);",id_String,id_String);
    strcat(getset_Interface,temp_String);
    
    sprintf(temp_String,"public String getDC%s() throws IllegalFieldValueException {\n\t\tif(%s.getText().equals(\"\"))\n\t\t\tthrow new IllegalFieldValueException(\"%s is empty\\n\");\n\t\ttry {\n\t\t\tFloat.parseFloat(%s.getText());\n\t\t}catch (Exception e) {\n\t\t\tthrow new IllegalFieldValueException(\"%s contains the illegal value: \" + %s.getText()+ \" it should contain an integer\"); \n\t\t}\n\t\treturn %s.getText();\n\t}\n\tpublic void setDC%s(String the_Text){\n\t\t%s.setText(the_Text);\n\t}\n\t", id_String, id_String, id_String, id_String, id_String, id_String, id_String, id_String, id_String);
    strcat(getset_Methods, temp_String);
    
    strcat(mysql_Entry, id_String);
    strcat(mysql_Entry, " DOUBLE PRECISION(20,10),");
    
    strcat(boolean_Fields, "check_Table(\"");
    strcat(boolean_Fields, id_String);
    strcat(boolean_Fields, "\" , \"DOUBLE\") && ");
    
    strcat(action_ADD, "+ getDC");
    strcat(action_ADD, id_String);
    strcat(action_ADD, "() + \"', '\"");
    
    sprintf(temp_String, "\n\t\ttry{\n\t\t\tfind_String = find_String + \"%s='\" + getDC%s() + \"' AND \";\n\t\t}catch (Exception e){\n\t\t    if (e.getLocalizedMessage().contains(\"illegal\"))\n\t\t\tthrow new Exception(e.getLocalizedMessage());}", id_String,id_String,id_String);
    strcat(action_DELETE, temp_String);
    
    sprintf(temp_String,"\"%s = \" + rs.getString(\"%s\") +  \",   \" + ", id_String, id_String);
    strcat(confirm_Delete, temp_String);
    
    sprintf(temp_String,"setDC%s(rs.getString(\"%s\"));\n\t\t\t", id_String, id_String);
    strcat(action_QUERY, temp_String);
    
    sprintf(temp_String, "\n\t\ttry{\n\t\t\treplace_String = replace_String + \"%s='\" + getDC%s() + \"', \";\n\t\t}catch (Exception e){\n\t\t    if (e.getLocalizedMessage().contains(\"illegal\"))\n\t\t\tthrow new Exception(e.getLocalizedMessage());}", id_String,id_String,id_String);
    strcat(action_UPDATE, temp_String)
}



|name '=' ID        {
    if (strcmp(temp_String2, "ADD") == 0 || strcmp(temp_String2, "DELETE") == 0 || strcmp(temp_String2, "QUERY") == 0 || strcmp(temp_String2, "UPDATE") == 0){
        button_Error = 1;
        yyerror("error");
    }
    
    entry_Count++;
    strcat(set_Buttons,"public JButton ");
    strcat(set_Buttons,id_String);
    strcat(set_Buttons," = new JButton(\"");
    strcat(set_Buttons,id_String);
    strcat(set_Buttons,"\");\n\t");
    
    strcat(add_Buttons,"Add_button(");
    strcat(add_Buttons,id_String);
    strcat(add_Buttons,", new ");
    strcat(add_Buttons, $3);
    strcat(add_Buttons,"(this));\n\t");
    
    free($3);
}

name:
ID      {int i; strcat(id_String,$1);free($1); for(i = 0; i < strlen(id_String);i++)temp_String2[i] = toupper(id_String[i]);temp_String2[i] = '\0';}
|name ID  {int i; strcat(id_String,$2);free($2); for(i = 0; i < strlen(id_String);i++)temp_String2[i] = toupper(id_String[i]);temp_String2[i] = '\0';}

%%

int actual_Error = 0;

int main(int argc, char * argv[]){
    int i, j, resume = 0, difference = 0;
    char project_Name[100], next_Hundred[100], FrameWork[25000], Duplicate[15000], c, *start;
    FILE * the_Framework, *the_Config, *the_Output;
    mode_t process_Mask;
    
    if (argc != 2){
        return -1;
    }
    
    set_Fields[0] = '\0';
    get_Fields[0] = '\0';
    add_Fields[0] = '\0';
    set_Buttons[0] = '\0';
    add_Buttons[0] = '\0';
    getset_Interface[0] = '\0';
    getset_Methods[0] = '\0';
    id_String[0] = '\0';
    project_Name[0] = '\0';
    mysql_Entry[0] = '\0';
    boolean_Fields[0] = '\0';
    action_ADD[0] = '\0';
    action_DELETE[0] = '\0';
    action_QUERY[0] = '\0';
    
    
    /*Check to see if in different directory (I couldn't get around this problem)*/
    start = strtok(argv[1], "/");
    strcpy(project_Name,start);
    if ((start = strtok(NULL,"/")) != NULL){
        return -1;
    }
    
    
    /*Check to see if a .config file*/
    for(i = 0; i < strlen(project_Name); i++){
        if (project_Name[i] == '.'){
            if (strcmp(&project_Name[i], ".config") != 0){
                printf("You need to include the file to be converted in the command line argument\n");
                return -1;
            };
        }
    }
    
    /*Open and parse the file*/
    the_Config = fopen(argv[1], "r");
    if (the_Config != NULL){
        yyin = the_Config;
        while (yyparse() == 0){
            id_String[0] = '\0';
            if (actual_Error > 0){
                if (button_Error == 1)
                    return -2;
                else
                    return entry_Count+1;
            }
        }
        yylex_destroy(yylval);
        fclose(the_Config);
    }
    else
        return -1;
    
    
    
    
    strcpy(project_Name, strtok(argv[1],"."));
    /*Check to see if there is anything to be added*/
    if (add_Fields[0] != '\0'){
        the_Framework = fopen("src/FrameWork.txt", "r");
        if (the_Framework == NULL){
            return -1;
        }
        
        i = 0;
        while((c = fgetc(the_Framework)) != EOF){
            FrameWork[i] = c;
            Duplicate[i] = c;
            i++;
        }
        fclose(the_Framework);
        
        FrameWork[i] = '\0';
        Duplicate[i] = '\0';
        for(i = 0; i < strlen(FrameWork); i ++ ,resume++){
            
            /*--------------------------------------------------------------------------*/
            if (FrameWork[i] == 'A' &&  FrameWork[i+1] == 'D' && FrameWork[i+2] == 'D'){
                c = FrameWork[i+9];
                FrameWork[i+9] = '\0';
                
                /*If it matches addFields replace with add_Fields string*/
                if(strcmp(&FrameWork[i], "ADDFIELDS") == 0 ){
                    FrameWork[i] = '\0';
                    strcat(FrameWork, add_Fields);
                    resume += 9;
                    
                    i = strlen(FrameWork);
                    difference = i - resume;
                    
                    for(j = resume; j < strlen(Duplicate); j++, i ++){
                        FrameWork[i] = Duplicate[j];
                    }
                    FrameWork[i] = '\0';
                    i = resume + difference;
                }
                /*If it matches ADDBUTTON replace with add_buttons string*/
                else if(strcmp(&FrameWork[i], "ADDBUTTON") == 0){
                    FrameWork[i] = '\0';
                    strcat(FrameWork, add_Buttons);
                    resume += 9;
                    
                    i = strlen(FrameWork);
                    difference = i - resume;
                    
                    for(j = resume; j < strlen(Duplicate); j++, i ++){
                        FrameWork[i] = Duplicate[j];
                    }
                    FrameWork[i] = '\0';
                    i = resume + difference;
                }
                else{
                    FrameWork[i+9] = c;
                }
            }
            /*--------------------------------------------------------------------------*/
            
            
            
            /*--------------------------------------------------------------------------*/
            else if (FrameWork[i] == 'S' &&  FrameWork[i+1] == 'E' && FrameWork[i+2] == 'T'){
                c = FrameWork[i+9];
                FrameWork[i+9] = '\0';
                
                /*If it matches serFields replace with set_Fields string*/
                if(strcmp(&FrameWork[i], "SETFIELDS") == 0 ){
                    FrameWork[i+9] = c;
                    FrameWork[i] = '\0';
                    strcat(FrameWork, set_Fields);
                    resume += 9;
                    
                    i = strlen(FrameWork);
                    difference = i - resume;
                    
                    for(j = resume; j < strlen(Duplicate); j++, i ++){
                        FrameWork[i] = Duplicate[j];
                    }
                    FrameWork[i] = '\0';
                    i = resume+difference;
                }
                /*If it matches setBUTTON replace with set_buttons string*/
                else if(strcmp(&FrameWork[i], "SETBUTTON") == 0){
                    FrameWork[i] = '\0';
                    strcat(FrameWork, set_Buttons);
                    resume += 9;
                    
                    
                    i = strlen(FrameWork);
                    difference = i - resume;
                    
                    for(j = resume; j < strlen(Duplicate); j++, i ++){
                        FrameWork[i] = Duplicate[j];
                    }
                    FrameWork[i] = '\0';
                    i = resume+difference;
                }
                else{
                    FrameWork[i+9] = c;
                }
            }
            /*--------------------------------------------------------------------------*/
            
            
            
            
            /*--------------------------------------------------------------------------*/
            else if (FrameWork[i] == 'G' &&  FrameWork[i+1] == 'E' && FrameWork[i+2] == 'T'){
                c = FrameWork[i+9];
                FrameWork[i+9] = '\0';
                
                /*If it matches GETANDSET replace with getset_Methods string*/
                if(strcmp(&FrameWork[i], "GETANDSET") == 0 ){
                    FrameWork[i] = '\0';
                    strcat(FrameWork, getset_Methods);
                    resume += 9;
                    
                    i = strlen(FrameWork);
                    difference = i - resume;
                    
                    for(j = resume; j < strlen(Duplicate); j++, i ++){
                        FrameWork[i] = Duplicate[j];
                    }
                    FrameWork[i] = '\0';
                    i = resume+difference;
                }else{
                    FrameWork[i+9] = c;
                }
            }
            /*--------------------------------------------------------------------------*/
            
            
            
            /*--------------------------------------------------------------------------*/
            else if (FrameWork[i] == 'T' &&  FrameWork[i+1] == 'H' && FrameWork[i+2] == 'E'){
                c = FrameWork[i+10];
                FrameWork[i+10] = '\0';
                
                /*If it matches THEPROJECT replace with project_Name string*/
                if(strcmp(&FrameWork[i], "THEPROJECT") == 0 ){
                    FrameWork[i] = '\0';
                    strcat(FrameWork, project_Name);
                    resume += 10;
                    
                    i = strlen(FrameWork);
                    difference = i - resume;
                    
                    for(j = resume; j < strlen(Duplicate); j++, i ++){
                        FrameWork[i] = Duplicate[j];
                    }
                    FrameWork[i] = '\0';
                    i = resume+difference;
                    /*If it matches THEPROJNAM replace with project_Title string*/
                }else if (strcmp(&FrameWork[i], "THEPROJNAM") == 0 ){
                    FrameWork[i] = '\0';
                    strcat(FrameWork, project_Title);
                    resume += 10;
                    
                    i = strlen(FrameWork);
                    difference = i - resume;
                    
                    for(j = resume; j < strlen(Duplicate); j++, i ++){
                        FrameWork[i] = Duplicate[j];
                    }
                    FrameWork[i] = '\0';
                    i = resume+difference;
                }
                else
                    FrameWork[i+10] = c;
            }
            /*--------------------------------------------------------------------------*/
            
            
            
            /*--------------------------------------------------------------------------*/
            else if (FrameWork[i] == 'N' &&  FrameWork[i+1] == 'U' && FrameWork[i+2] == 'M'){
                c = FrameWork[i+6];
                FrameWork[i+6] = '\0';
                
                /*If it matches THEPROJECT replace with project_Name string*/
                if(strcmp(&FrameWork[i], "NUMBER") == 0 ){
                    FrameWork[i] = '\0';
                    sprintf(&FrameWork[i], "%d", number_Field);
                    resume += 6;
                    
                    i = strlen(FrameWork);
                    difference = i - resume;
                    
                    for(j = resume; j < strlen(Duplicate); j++, i ++){
                        FrameWork[i] = Duplicate[j];
                    }
                    FrameWork[i] = '\0';
                    i = resume+difference;
                }
                else
                    FrameWork[i+6] = c;
            }
            /*--------------------------------------------------------------------------*/
            
            
            
            /*--------------------------------------------------------------------------*/
            else if (FrameWork[i] == 'S' &&  FrameWork[i+1] == 'Q' && FrameWork[i+2] == 'L'){
                c = FrameWork[i+8];
                FrameWork[i+8] = '\0';
                
                /*If it matches SQLENTRY replace with mysql_Entry string*/
                if(strcmp(&FrameWork[i], "SQLENTRY") == 0 ){
                    FrameWork[i] = '\0';
                    strcat(FrameWork, mysql_Entry);
                    resume += 8;
                    
                    i = strlen(FrameWork);
                    difference = i - resume;
                    
                    for(j = resume; j < strlen(Duplicate); j++, i ++){
                        FrameWork[i] = Duplicate[j];
                    }
                    FrameWork[i] = '\0';
                    i = resume+difference;
                }else
                    FrameWork[i+8] = c;
            }
            /*--------------------------------------------------------------------------*/
            
            
            
            /*--------------------------------------------------------------------------*/
            else if (FrameWork[i] == 'B' &&  FrameWork[i+1] == 'O' && FrameWork[i+2] == 'O'){
                c = FrameWork[i+8];
                FrameWork[i+8] = '\0';
                
                /*If it matches BOOCHECK replace with boolean_Fields string*/
                if(strcmp(&FrameWork[i], "BOOCHECK") == 0 ){
                    FrameWork[i] = '\0';
                    boolean_Fields[strlen(boolean_Fields) -4] = '\0';
                    strcat(FrameWork, boolean_Fields);
                    resume += 8;
                    
                    i = strlen(FrameWork);
                    difference = i - resume;
                    
                    for(j = resume; j < strlen(Duplicate); j++, i ++){
                        FrameWork[i] = Duplicate[j];
                    }
                    FrameWork[i] = '\0';
                    i = resume+difference;
                }else
                    FrameWork[i+8] = c;
            }
            /*--------------------------------------------------------------------------*/
            
            
            
            /*--------------------------------------------------------------------------*/
            else if (FrameWork[i] == 'C' &&  FrameWork[i+1] == 'O' && FrameWork[i+2] == 'N'){
                c = FrameWork[i+7];
                FrameWork[i+7] = '\0';
                
                /*If it matches CONFIRM replace with confirm_Delete string*/
                if(strcmp(&FrameWork[i], "CONFIRM") == 0 ){
                    FrameWork[i] = '\0';
                    strcat(FrameWork, confirm_Delete);
                    resume += 7;
                    
                    i = strlen(FrameWork);
                    difference = i - resume;
                    
                    for(j = resume; j < strlen(Duplicate); j++, i ++){
                        FrameWork[i] = Duplicate[j];
                    }
                    FrameWork[i] = '\0';
                    i = resume+difference;
                }else
                    FrameWork[i+7] = c;
            }
            /*--------------------------------------------------------------------------*/
            
            
            
            /*--------------------------------------------------------------------------*/
            else if (FrameWork[i] == 'A' &&  FrameWork[i+1] == 'C' && FrameWork[i+2] == 'T'){
                c = FrameWork[i+9];
                FrameWork[i+9] = '\0';
                
                /*If it matches ACTIONADD replace with action_ADD string*/
                if(strcmp(&FrameWork[i], "ACTIONADD") == 0 ){
                    FrameWork[i] = '\0';
                    action_ADD[strlen(action_ADD)-8] = '\0';
                    strcat(FrameWork, action_ADD);
                    resume += 9;
                    
                    i = strlen(FrameWork);
                    difference = i - resume;
                    
                    for(j = resume; j < strlen(Duplicate); j++, i ++){
                        FrameWork[i] = Duplicate[j];
                    }
                    FrameWork[i] = '\0';
                    i = resume+difference;
                }else if(strcmp(&FrameWork[i], "ACTIONDEL") == 0 ){
                    FrameWork[i] = '\0';
                    strcat(FrameWork, action_DELETE);
                    resume += 9;
                    
                    i = strlen(FrameWork);
                    difference = i - resume;
                    
                    for(j = resume; j < strlen(Duplicate); j++, i ++){
                        FrameWork[i] = Duplicate[j];
                    }
                    FrameWork[i] = '\0';
                    i = resume+difference;
                }else if(strcmp(&FrameWork[i], "ACTIONQUE") == 0 ){
                    FrameWork[i] = '\0';
                    strcat(FrameWork, action_QUERY);
                    resume += 9;
                    
                    i = strlen(FrameWork);
                    difference = i - resume;
                    
                    for(j = resume; j < strlen(Duplicate); j++, i ++){
                        FrameWork[i] = Duplicate[j];
                    }
                    FrameWork[i] = '\0';
                    i = resume+difference;
                } else if(strcmp(&FrameWork[i], "ACTIONUPD") == 0 ){
                    FrameWork[i] = '\0';
                    strcat(FrameWork, action_UPDATE);
                    resume += 9;
                    
                    i = strlen(FrameWork);
                    difference = i - resume;
                    
                    for(j = resume; j < strlen(Duplicate); j++, i ++){
                        FrameWork[i] = Duplicate[j];
                    }
                    FrameWork[i] = '\0';
                    i = resume+difference;
                }else
                    FrameWork[i+9] = c;
            }
            /*--------------------------------------------------------------------------*/
            
        }
        
        
        
        
        /*Setting the typical pemissions and making the directory*/
        process_Mask = umask(022);
        mkdir(project_Name, S_IRWXU | S_IRWXG | S_IRWXO);
        umask(process_Mask);
        
        strcpy(temp_String, "./");
        strcat(temp_String,project_Name);
        strcat(temp_String, "/");
        strcat(temp_String,project_Name);
        strcat(temp_String, ".java");
        the_Output = fopen(temp_String, "w");
        if (the_Output == NULL){
            return -1;
        }
        else{
            fprintf(the_Output, FrameWork);
            fclose(the_Output);
        }
        
        strcpy(temp_String, "./");
        strcat(temp_String,project_Name);
        strcat(temp_String, "/");
        strcat(temp_String,project_Name);
        strcat(temp_String, "FieldEdit.java");
        the_Output = fopen(temp_String, "w");
        if (the_Output == NULL){
            return -1;
        }
        else{
            sprintf(temp_String, "public interface %sFieldEdit{", project_Name);
            fprintf(the_Output, temp_String);
            fprintf(the_Output, getset_Interface);
            fprintf(the_Output,"\n\tpublic void appendToStatusArea(String message);\n}");
            fclose(the_Output);
        }
    }
    return 0;
}

int yyerror(const char * s){
    actual_Error ++;
}
