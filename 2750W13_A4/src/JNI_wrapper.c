#include <stdlib.h>
#include <stdio.h>
#include <string.h>
#include "jni.h"
#include "ParameterManager.h"
#include "Dialogc.h"
#include "Queue.h"


int Parse_text(ParameterManager *the_Manager,char *the_Text, int needed[3]);


ParameterManager * the_Manager;
int needed[3];
int field_End;



JNIEXPORT void JNICALL Java_Dialogc_Reset_1manager(JNIEnv * env, jobject obj){
    PM_destroy(the_Manager);
    needed[0] = 0;
    needed[1] = 0;
    needed[2] = 0;
}

/*Return's the field associated with a number*/
JNIEXPORT jstring JNICALL Java_Dialogc_Get_1fields(JNIEnv *env , jobject obj, jint the_Position){
    if (the_Position <= (needed[0] -1)){
        if (needed[2] == 1)
            return (*env)->NewStringUTF(env,Map_manager(the_Manager, the_Position+1));
        else
            return (*env)->NewStringUTF(env,Map_manager(the_Manager, the_Position+1+needed[1]));
    }
    return NULL;
}

/*Return's the button associated with a number*/
JNIEXPORT jstring JNICALL Java_Dialogc_Get_1buttons(JNIEnv *env , jobject obj, jint the_Position){
    if (the_Position <= (needed[1] -1)){
        if (needed[2] == -1)
            return (*env)->NewStringUTF(env,Map_manager(the_Manager, the_Position+1));
        else
            return (*env)->NewStringUTF(env,Map_manager(the_Manager, the_Position+1+needed[0]));
    }
    return NULL;
}

/*Return's the value associated with a string */
JNIEXPORT jstring JNICALL Java_Dialogc_Get_1value(JNIEnv *env, jobject obj, jstring field_Name){
    char *the_Name = (char *)(*env)->GetStringUTFChars(env, field_Name, 0);
    
    union param_value the_Value = PM_getValue(the_Manager,the_Name);
    (*env)->ReleaseStringUTFChars(env, field_Name, the_Name);
    
    return (*env)->NewStringUTF(env, the_Value.str_val);
}




JNIEXPORT jint JNICALL Java_Dialogc_Initialize_1parserIDE(JNIEnv *env, jobject obj, jstring java_String){
    char *the_Text = (char *)(*env)->GetStringUTFChars(env, java_String, 0);
    int i, j, colon_Count = 0, equal_Count = 0, comma_Count = 0, EOF_Location = -1;
    union param_value setup_Lists;
    char * the_Fields;
    
    
    /*looping through and confirming we have the appropriate number of ; = and , */
    for(i = 0; i < strlen(the_Text); i ++){
        if (the_Text[i] == '#'){
            if (the_Text[i+1] == '!' && the_Text[i+2] == '#')
                EOF_Location = i+2;
            else
                while(the_Text[i] != '\n')
                    i++;
        }
        else if (the_Text[i] == '{'){
            comma_Count++;
            for(j = i; the_Text[j] != '}'; j++){
                if(the_Text[j] ==  ','){
                    comma_Count++;
                }
            }
        }
        else if (the_Text[i] == ';')
            colon_Count++;
        else if (the_Text[i] == '=')
            equal_Count++;
    }
    if (EOF_Location == -1){
        (*env)->ReleaseStringUTFChars(env, java_String, the_Text);
        return -6;
    }
    if (colon_Count != equal_Count){
        (*env)->ReleaseStringUTFChars(env, java_String, the_Text);
        return -1;
    }
    if ((comma_Count + 3) < colon_Count){
        (*env)->ReleaseStringUTFChars(env, java_String, the_Text);
        return -2;
    }
    if ((comma_Count + 3) > colon_Count){
        (*env)->ReleaseStringUTFChars(env, java_String, the_Text);
        return -3;
    }
    
    
    the_Manager = PM_create(colon_Count);
    
    
    /*listing the three defaults every file should have*/
    PM_manage(the_Manager,"title",STRING_TYPE,1);
    PM_manage(the_Manager,"fields",LIST_TYPE,1);
    PM_manage(the_Manager,"buttons",LIST_TYPE,1);
    
    /*Preparing and parsing the text*/
    the_Text[EOF_Location] = '\0';
    if (PM_parseFrom(the_Manager, the_Text, '#') == 0){
        (*env)->ReleaseStringUTFChars(env, java_String, the_Text);
        PM_destroy(the_Manager);
        return -4;
    }
    
    /*Gets the list of fields and manages them*/
    setup_Lists = PM_getValue(the_Manager, "fields");
    the_Fields = PL_next(setup_Lists.list_val);
    field_End = 2;
    while (the_Fields != NULL){
        if (PM_manage(the_Manager,the_Fields,STRING_TYPE,1) == 0){
            (*env)->ReleaseStringUTFChars(env, java_String, the_Text);
            PM_destroy(the_Manager);
            return -4;
        }
        free(the_Fields);
        the_Fields = PL_next(setup_Lists.list_val);
        field_End++;
    }
    /*Gets the buttons and manages them*/
    setup_Lists = PM_getValue(the_Manager, "buttons");
    the_Fields = PL_next(setup_Lists.list_val);
    while (the_Fields != NULL){
        if (PM_manage(the_Manager,the_Fields,STRING_TYPE,1) == 0){
            (*env)->ReleaseStringUTFChars(env, java_String, the_Text);
            PM_destroy(the_Manager);
            return -4;
        }
        free(the_Fields);
        the_Fields = PL_next(setup_Lists.list_val);
    }
    
    /*Adjusts the file for parsing the rest of the file */
    the_Text[EOF_Location] = ' ';
    for (i = 0; the_Text[i + EOF_Location] != '\0'; i++)
        the_Text[i] = the_Text[i + EOF_Location];
    the_Text[i] = '\0';
    
    
    /*Parse's the rest of the file into the now managed parameters */
    if (PM_parseFrom(the_Manager, the_Text, '#') == 0){
        (*env)->ReleaseStringUTFChars(env, java_String, the_Text);
        PM_destroy(the_Manager);
        return -5;
    }
    
    (*env)->ReleaseStringUTFChars(env, java_String, the_Text);
    return colon_Count;
}

JNIEXPORT void JNICALL Java_Dialogc_Reset_1managerIDE(JNIEnv * env, jobject obj){
    PM_destroy(the_Manager);
}

/*Return's the field associated with a number */
JNIEXPORT jstring JNICALL Java_Dialogc_Get_1fieldsIDE(JNIEnv *env , jobject obj, jint the_Position){
    if (the_Position + 3 <= field_End)
        return (*env)->NewStringUTF(env,Map_manager(the_Manager, the_Position+3));
    return NULL;
}

/*Return's the button associated with a number*/
JNIEXPORT jstring JNICALL Java_Dialogc_Get_1buttonsIDE(JNIEnv *env , jobject obj, jint the_Position){
    return (*env)->NewStringUTF(env,Map_manager(the_Manager, the_Position+field_End+1));
}


