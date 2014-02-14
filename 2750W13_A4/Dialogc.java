
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.filechooser.FileNameExtensionFilter;

public class Dialogc extends JFrame {

    private int Height = 750, Width = 650;
    private boolean Modified = false, saved = true;
    private JTextArea Input = new JTextArea();
    private JLabel current_Save = new JLabel("Current Project : ");
    private File current_File;
    private String java_Compiler = "javac";
    private String compile_Settings = "";
    private String java_run_Time = "java";
    private String run_time_Options = "";
    private String working_Directory = ".";
    private String README = "";
    private Boolean IDE = false;

    static {
        System.loadLibrary("Dialogc");
    }

    /**
     * Resets the global manager that was created in the JNI wrapper
     */
    native void Reset_manager();

    /**
     * Creates a new Parameter manager/parser
     *
     * @param the_Text the text to be parsed.
     * @return an error return value.
     */
    native int Initialize_parserIDE(String the_Text);

    /**
     * Gets the value associated with the_Number.
     *
     * @param the_Number The number
     * @return The name of the v at that number.
     */
    native String Get_IDIDE(int the_Number);

    /**
     * Gets the field associated with field_Number.
     *
     * @param field_Number The field number
     * @return The name of the field at that number.
     */
    native String Get_fieldsIDE(int field_Number);

    /**
     * Gets the button associated with button_Number.
     *
     * @param button_Number The button number
     * @return The name of the button at that number.
     */
    native String Get_buttonsIDE(int button_Number);

    /**
     * Gets the value associated with a field or number
     *
     * @param the_Field The string to be found.
     * @return The value associated with that string
     */
    native String Get_value(String the_Field);

    public Dialogc() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
        }

        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                JButton Click = new JButton();
                Click.addActionListener(new Quit_action());
                Click.doClick();
            }
        });
        this.setResizable(false);
        this.setSize(Width, Height);
        this.setLayout(new BorderLayout());
        this.setLocationByPlatform(true);

        current_Save.setFont(new Font(current_Save.getFont().getName(), Font.PLAIN, 14));
        current_Save.setHorizontalAlignment(SwingConstants.CENTER);

        Input.setLineWrap(false);
        Input.setSize(Width, Height);
        JScrollPane input_Scroll = new JScrollPane(Input);
        input_Scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        input_Scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        Input.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent de) {
                Modified = true;
                if (!current_Save.getText().contains("[Modified]")) {
                    current_Save.setText(current_Save.getText() + "[Modified]");
                }
            }

            @Override
            public void removeUpdate(DocumentEvent de) {
                Modified = true;
                if (!current_Save.getText().contains("[Modified]")) {
                    current_Save.setText(current_Save.getText() + "[Modified]");
                }
            }

            @Override
            public void changedUpdate(DocumentEvent de) {
                Modified = true;
                if (!current_Save.getText().contains("[Modified]")) {
                    current_Save.setText(current_Save.getText() + "[Modified]");
                }
            }
        });



        Draw_menu();
        Draw_ID();
        this.add(input_Scroll);
        this.add(current_Save, BorderLayout.SOUTH);
        this.setTitle("Dialogc");
        this.setVisible(true);
        Input.setFocusable(true);
        Input.requestFocusInWindow();
    }

    /**
     * Set's up the Menu bar with all the default operations.
     */
    private void Draw_menu() {
        JMenuBar menu_Bar = new JMenuBar();

        JMenu File = new JMenu("File");
        {
            JMenuItem New = new JMenuItem("New");
            JMenuItem Open = new JMenuItem("Open");
            JMenuItem Save = new JMenuItem("Save");
            JMenuItem save_As = new JMenuItem("Save As");
            JMenuItem Quit = new JMenuItem("Quit");

            New.addActionListener(new New_action());
            Open.addActionListener(new Open_action());
            Save.addActionListener(new Save_action());
            save_As.addActionListener(new Saveas_action());
            Quit.addActionListener(new Quit_action());


            New.setAccelerator(KeyStroke.getKeyStroke('N', KeyEvent.CTRL_DOWN_MASK));
            Open.setAccelerator(KeyStroke.getKeyStroke('O', KeyEvent.CTRL_DOWN_MASK));
            Save.setAccelerator(KeyStroke.getKeyStroke('S', KeyEvent.CTRL_DOWN_MASK));
            save_As.setAccelerator(KeyStroke.getKeyStroke('S', KeyEvent.ALT_DOWN_MASK));
            Quit.setAccelerator(KeyStroke.getKeyStroke('Q', KeyEvent.CTRL_DOWN_MASK));

            File.add(New);
            File.add(Open);
            File.add(new JSeparator());
            File.add(Save);
            File.add(save_As);
            File.add(new JSeparator());
            File.add(Quit);
        }


        JMenu Compile = new JMenu("Compile");
        {
            JMenuItem compile_Item = new JMenuItem("Compile");
            JMenuItem compile_Run = new JMenuItem("Compile and Run");

            compile_Item.addActionListener(new Compile_action());
            compile_Run.addActionListener(new Compile_run());

            compile_Item.setAccelerator(KeyStroke.getKeyStroke('C', KeyEvent.CTRL_DOWN_MASK));
            compile_Run.setAccelerator(KeyStroke.getKeyStroke('R', KeyEvent.CTRL_DOWN_MASK));

            Compile.add(compile_Item);
            Compile.add(compile_Run);
        }
        JMenu Config = new JMenu("Config");
        {
            JMenuItem java_Compiler = new JMenuItem("Java Compiler");
            JMenuItem compile_Options = new JMenuItem("Compile Options");
            JMenuItem java_Run = new JMenuItem("Java Run Time");
            JMenuItem run_Time = new JMenuItem("Run Time Options");
            JMenuItem Directory = new JMenuItem("Working Directory");
            JMenuItem compile_Mode = new JMenuItem("Compile Mode");

            java_Compiler.setAccelerator(KeyStroke.getKeyStroke('J', KeyEvent.ALT_DOWN_MASK));
            compile_Options.setAccelerator(KeyStroke.getKeyStroke('C', KeyEvent.ALT_DOWN_MASK));
            java_Run.setAccelerator(KeyStroke.getKeyStroke('T', KeyEvent.ALT_DOWN_MASK));
            run_Time.setAccelerator(KeyStroke.getKeyStroke('O', KeyEvent.ALT_DOWN_MASK));
            Directory.setAccelerator(KeyStroke.getKeyStroke('D', KeyEvent.ALT_DOWN_MASK));
            compile_Mode.setAccelerator(KeyStroke.getKeyStroke('M', KeyEvent.ALT_DOWN_MASK));

            java_Compiler.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent ae) {
                    Set_compiler();
                }
            });
            compile_Options.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent ae) {
                    try {
                        compile_Settings = JOptionPane.showInputDialog(null, "Current compile options : ", compile_Settings);
                        compile_Settings = compile_Settings.trim();
                    } catch (Exception e) {
                    };
                }
            });
            java_Run.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent ae) {
                    Set_run();
                }
            });
            run_Time.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent ae) {
                    try {
                        run_time_Options = JOptionPane.showInputDialog(null, "Current run time options : ", run_time_Options);
                        run_time_Options = run_time_Options.trim();
                    } catch (Exception e) {
                    };
                }
            });
            Directory.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent ae) {
                    try {
                        working_Directory = JOptionPane.showInputDialog(null, "Current working directory : ", working_Directory);
                        working_Directory = working_Directory.trim();
                    } catch (Exception e) {
                    };
                }
            });
            compile_Mode.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent ae) {
                    Set_parser();
                }
            });

            Config.add(java_Compiler);
            Config.add(compile_Options);
            Config.add(java_Run);
            Config.add(run_Time);
            Config.add(Directory);
            Config.add(compile_Mode);

        }
        JMenu Help = new JMenu("Help");
        {
            JMenuItem help_Item = new JMenuItem("Help");
            JMenuItem About = new JMenuItem("About");

            Scanner fileIn = null;


            try {
                fileIn = new Scanner(new FileInputStream("./README.txt"));
                while (fileIn.hasNext()) {
                    README = (README + fileIn.nextLine() + "\n");
                }

            } catch (FileNotFoundException e) {
                System.exit(0);
            }
            fileIn.close();


            help_Item.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent ae) {
                    JOptionPane.showMessageDialog(new JFrame(), README, "About", JOptionPane.OK_OPTION);
                }
            });
            About.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent ae) {
                    JOptionPane.showMessageDialog(new JFrame(), "Name : Rick Knoop\nID : 0755307\nClass : CIS 2750", "About", JOptionPane.OK_OPTION);
                }
            });

            help_Item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F1, KeyEvent.CTRL_DOWN_MASK));
            About.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F2, KeyEvent.CTRL_DOWN_MASK));


            Help.add(help_Item);
            Help.add(About);
        }


        menu_Bar.add(File);
        menu_Bar.add(Compile);
        menu_Bar.add(Config);
        menu_Bar.add(Help);


        setJMenuBar(menu_Bar);
    }

    /**
     * Draws the buttons with the default symbols.
     */
    private void Draw_ID() {
        JPanel button_Panel = new JPanel();

        button_Panel.setPreferredSize(new Dimension(Width, 40));
        try {
            JButton New = new JButton(new ImageIcon(ImageIO.read(new File("src/Icons/New.png"))));
            JButton Open = new JButton(new ImageIcon(ImageIO.read(new File("src/Icons/Open.png"))));
            JButton Save = new JButton(new ImageIcon(ImageIO.read(new File("src/Icons/Save.png"))));
            JButton save_As = new JButton(new ImageIcon(ImageIO.read(new File("src/Icons/Saveas.png"))));
            JButton Compile = new JButton(new ImageIcon(ImageIO.read(new File("src/Icons/Gear.png"))));
            JButton Compile_run = new JButton(new ImageIcon(ImageIO.read(new File("src/Icons/Play.png"))));

            New.addActionListener(new New_action());
            Open.addActionListener(new Open_action());
            Save.addActionListener(new Save_action());
            save_As.addActionListener(new Saveas_action());
            Compile.addActionListener(new Compile_action());
            Compile_run.addActionListener(new Compile_run());

            button_Panel.add(New);
            button_Panel.add(Open);
            button_Panel.add(Save);
            button_Panel.add(save_As);
            button_Panel.add(Compile);
            button_Panel.add(Compile_run);
        } catch (Exception e) {
        }

        button_Panel.setBackground(Color.RED);
        this.add(button_Panel, BorderLayout.NORTH);
    }

    /**
     * Save's the text from input to the file and sets the default file
     * (current_File) to the file it's been passed.
     *
     * @param output_File The file to be saved to.
     */
    private void Save_file(File output_File) {
        PrintWriter fileOut = null;

        if (output_File != null) {
            try {
                fileOut = new PrintWriter(new FileOutputStream(output_File));
                fileOut.print(Input.getText());
                current_File = output_File;
            } catch (FileNotFoundException e) {
                System.exit(0);
            }
            fileOut.close();
        }
    }

    /**
     * Open's the passed file and display's it's text to the input panel, also
     * sets the default file (current_File) to the file it's been passed.
     *
     * @param input_File The file to be opened
     */
    private void Open_file(File input_File) {
        Scanner fileIn = null;

        if (input_File != null) {
            Input.setText("");
            try {
                fileIn = new Scanner(new FileInputStream(input_File));
                while (fileIn.hasNext()) {
                    Input.setText(Input.getText() + fileIn.nextLine() + "\n");
                }
                current_File = input_File;
            } catch (FileNotFoundException e) {
                System.exit(0);
            }
            fileIn.close();
        }
    }

    public static void main(String[] args) {
        Dialogc the_IDE = new Dialogc();
    }

    /*
     * All of the action listeners for the buttons and menu command's
     */
    private class New_action implements ActionListener {

        public void actionPerformed(ActionEvent ae) {
            if (Modified == true) {
                if (JOptionPane.showConfirmDialog(new JFrame(), "Do you wish to save the file before starting a new File?", "", JOptionPane.YES_NO_OPTION) == 0) {
                    JButton Click = new JButton();
                    Click.addActionListener(new Save_action());
                    Click.doClick();
                }
            }
            Input.setText("");
            Modified = false;
            current_Save.setText("Current Project : ");
            current_File = null;

        }
    }

    private class Open_action implements ActionListener {

        public void actionPerformed(ActionEvent ae) {
            if (Modified == true) {
                if (JOptionPane.showConfirmDialog(new JFrame(), "Do you wish to save the file before opening?", "", JOptionPane.YES_NO_OPTION) == 0) {
                    JButton Click = new JButton();
                    Click.addActionListener(new Save_action());
                    Click.doClick();
                }
            }
            int return_Val = -1;
            JFileChooser open_Dialog = new JFileChooser();

            open_Dialog.setCurrentDirectory(new java.io.File("."));
            FileNameExtensionFilter filter = new FileNameExtensionFilter("*.config", "config");
            open_Dialog.setAcceptAllFileFilterUsed(false);
            open_Dialog.setFileFilter(filter);
            open_Dialog.setFileHidingEnabled(true);
            return_Val = open_Dialog.showOpenDialog(null);

            if (return_Val == 0) {
                if (open_Dialog.getSelectedFile().getName().toLowerCase().endsWith(".config")) {
                    Open_file(open_Dialog.getSelectedFile());
                    Modified = false;
                    current_Save.setText("Current Project: " + current_File.getPath());
                }
            }
        }
    }

    private class Save_action implements ActionListener {

        public void actionPerformed(ActionEvent ae) {
            if (current_File != null) {
                Modified = false;
                current_Save.setText(current_Save.getText().replace("[Modified]", ""));
                Save_file(current_File);
            } else {
                //Very convoluted way of calling save as because I couldn't find a different way
                JButton Click = new JButton();
                Click.addActionListener(new Saveas_action());
                Click.doClick();
            }
        }
    }

    private class Saveas_action implements ActionListener {

        public void actionPerformed(ActionEvent ae) {
            int return_Val = -1;
            JFileChooser save_Dialog = new JFileChooser();

            save_Dialog.setCurrentDirectory(new java.io.File("."));
            FileNameExtensionFilter filter = new FileNameExtensionFilter("*.config", "config");
            save_Dialog.setAcceptAllFileFilterUsed(false);
            save_Dialog.setFileFilter(filter);
            save_Dialog.setFileHidingEnabled(true);
            return_Val = save_Dialog.showSaveDialog(null);

            if (return_Val == 0) {
                if (save_Dialog.getSelectedFile().getName().toLowerCase().endsWith(".config")) {
                    Save_file(save_Dialog.getSelectedFile());
                } else {
                    Save_file(new File(save_Dialog.getSelectedFile().getName() + ".config"));
                }
                Modified = false;
                current_Save.setText("Current Project: " + current_File.getPath());

            }
        }
    }

    private class Compile_action implements ActionListener {

        public void actionPerformed(ActionEvent ae) {
            int result = 0;
            Process p;

            if (current_File == null || Modified == true) {
                if (JOptionPane.showConfirmDialog(new JFrame(), "You need to save before compiling, do you wish to save?", "Error", JOptionPane.YES_NO_OPTION) == 0) {
                    JButton Click = new JButton();
                    Click.addActionListener(new Save_action());
                    Click.doClick();
                }
            }
            if (current_File != null && Modified != true) {
                String the_Project = current_File.getName().replace(".config", "");
                File output = new File(working_Directory + "/" + the_Project + "/" + the_Project + ".java");
                if (output.exists()) {
                    if (JOptionPane.showConfirmDialog(new JFrame(), "The file arleady exists, do you wish to overwrite?", "", JOptionPane.YES_NO_OPTION) == 1) {
                        result = 1;
                        saved = false;
                    }
                }

                if (IDE == false && result != 1) {
                    saved = true;
                    try {
                        /*Moves the file if needed as yadc doesnt work in other directories*/
                        if (!new File(".").getAbsoluteFile().toString().equals(current_File.getAbsolutePath().replace(current_File.getName(), ".").toString())) {
                            p = Runtime.getRuntime().exec("cp " + current_File.getAbsolutePath() + " ./" + current_File.getName());
                            p.waitFor();
                        }
                        p = Runtime.getRuntime().exec("./yadc " + current_File.getName());
                        p.waitFor();
                        switch (p.exitValue()) {
                            case 255:
                                JOptionPane.showMessageDialog(new JFrame(), "There was an error opening one of the files.", "Error", JOptionPane.ERROR_MESSAGE);
                                break;
                                case 254:
                                JOptionPane.showMessageDialog(new JFrame(), "One of your buttons was called ADD, DELETE, UPDATE or QUERY, these names are reserved.", "Error", JOptionPane.ERROR_MESSAGE);
                                break;
                            case 0:
                                System.out.println("mv ./" + current_File.getName().replace(".config", "") + " " + working_Directory);
                                p = Runtime.getRuntime().exec("mv ./" + current_File.getName().replace(".config", "") + " " + working_Directory);
                                p.waitFor();

                                /*Move's the .jar only if needed*/
                                if (!new File(working_Directory + "/" + current_File.getName().replace(".config", "/mysql-connector-java-5.1.24-bin.jar")).exists()){
                                    p = Runtime.getRuntime().exec("cp ./mysql-connector-java-5.1.24-bin.jar " + working_Directory + "/" + current_File.getName().replace(".config", "/mysql-connector-java-5.1.24-bin.jar"));
                                    p.waitFor();
                                }


                                break;
                            default:
                                JOptionPane.showMessageDialog(new JFrame(), "There was an error in the " + p.exitValue() + " entry.", "Error", JOptionPane.ERROR_MESSAGE);
                                break;
                        }
                        if (!new File(".").getAbsoluteFile().toString().equals(current_File.getAbsolutePath().replace(current_File.getName(), ".").toString())) {
                            p = Runtime.getRuntime().exec("rm ./" + current_File.getName());
                            p.waitFor();
                        }

                    } catch (Exception e) {
                    }
                } else if (IDE = true && result != 1) {
                    saved = true;
                    switch (Initialize_parserIDE(Input.getText())) {
                        case -1:
                            JOptionPane.showMessageDialog(new JFrame(), "There was a value missing a  ';'  or  '='.", "Error", JOptionPane.ERROR_MESSAGE);
                            break;
                        case -2:
                            JOptionPane.showMessageDialog(new JFrame(), "There were more values than created at the start.", "Error", JOptionPane.ERROR_MESSAGE);
                            break;
                        case -3:
                            JOptionPane.showMessageDialog(new JFrame(), "There were more values created at the start than in the file.", "Error", JOptionPane.ERROR_MESSAGE);
                            break;
                        case -4:
                            JOptionPane.showMessageDialog(new JFrame(), "Something went wrong while creating the fields/buttons.", "Error", JOptionPane.ERROR_MESSAGE);
                            break;
                        case -5:
                            JOptionPane.showMessageDialog(new JFrame(), "Something went wrong while assigning the fields/buttons.", "Error", JOptionPane.ERROR_MESSAGE);
                            break;
                        case -6:
                            JOptionPane.showMessageDialog(new JFrame(), "You are missing an EOF location after setting the title, fields and buttons.", "Error", JOptionPane.ERROR_MESSAGE);
                            break;
                        default:
                            try {
                                String the_Text = "";
                                String set_Fields = "";
                                String set_Buttons = "";
                                String add_Fields = "";
                                String add_Buttons = "";
                                String getset_Methods = "";
                                String getset_Interface = "";
                                String value_Exception = "";
                                String possible_Value = "";
                                String current = "";
                                String boolean_Fields = "";
                                String sql_Entry = "";
                                String action_ADD = "";
                                String action_DELETE = "";
                                String action_QUERY = "";
                                String confirm_String = "";

                                int i = 0, j = 0, error = 0;


                                while ((current = Get_fieldsIDE(i)) != null) {
                                    value_Exception = "";
                                    possible_Value = Get_value(current);
                                    if (possible_Value.toLowerCase().equals("float")) {
                                        value_Exception = "\n\t\ttry {\n\t\t\tFloat.parseFloat(" + current + ".getText()); //Should throw an exception if not\n\t\t}catch (Exception e) {throw new IllegalFieldValueException(" + current + ".getText()); }";
                                        boolean_Fields = (boolean_Fields + "check_Table(\"" + current + "\" , \"DOUBLE\") && ");
                                        sql_Entry = (sql_Entry + current + " DOUBLE PRECISION(20,10),");
                                    } else if (possible_Value.toLowerCase().equals("integer")) {
                                        value_Exception = "\n\t\ttry {\n\t\t\tInteger.parseInt(" + current + ".getText()); //Should throw an exception if not\n\t\t}catch (Exception e) {throw new IllegalFieldValueException(" + current + ".getText()); }";
                                        boolean_Fields = (boolean_Fields + "check_Table(\"" + current + "\" , \"INT\") && ");
                                        sql_Entry = (sql_Entry + current + " INT,");
                                    } else if (!possible_Value.toLowerCase().equals("string")) {
                                        JOptionPane.showMessageDialog(new JFrame(), "The field: " + current + " is not a string, integer, or float; as a result compilation failed.", "Error", JOptionPane.ERROR_MESSAGE);
                                        error = 1;
                                        break;
                                    } else {
                                        boolean_Fields = (boolean_Fields + "check_Table(\"" + current + "\" , \"CHAR\") && ");
                                        sql_Entry = (sql_Entry + current + " CHAR(100),");
                                    }


                                    set_Fields = (set_Fields + "public JTextField " + current + " = new JTextField();\n\t\t");
                                    add_Fields = (add_Fields + "Add_field(\"" + current + "\" , " + current + ");\n\t\t");
                                    getset_Methods = (getset_Methods + "public String getDC" + current + "() throws IllegalFieldValueException {\n\t\tif(" + current + ".getText().equals(\"\"))\n\t\t\tthrow new IllegalFieldValueException(\"\");" + value_Exception + "\n\t\treturn " + current + ".getText();\n\t}\n\tpublic void setDC" + current + "(String the_Text){\n\t\t" + current + ".setText(the_Text);\n\t}\n\t");
                                    i++;
                                    getset_Interface = (getset_Interface + "\n\tpublic String getDC" + current + "() throws IllegalFieldValueException;\n\tpublic void setDC" + current + "(String the_Text);");
                                    action_ADD = action_ADD + " + getDC" + current + "() + \"', '\"";
                                    action_DELETE = action_DELETE + "\n\t\ttry{\n\t\t\tfind_String = find_String + \"" + current + "'\" + getDC" + current + "() + \"' AND \";\n\t\t}catch (Exception e){}";
                                    action_QUERY = action_QUERY + "setDC" + current + "(rs.getString(\"" + current + "\"));\n\t\t\t";
                                    confirm_String = confirm_String + "\"" + current + " = \" + rs.getString(\"" + current + "\") +  \",   \" + ";
                                }

                                boolean_Fields = boolean_Fields.substring(0, boolean_Fields.lastIndexOf(" && "));

                                if (error == 1) {
                                    break;
                                }

                                while ((current = Get_buttonsIDE(j)) != null) {
                                    if (new String("ADD DELETE QUERY UPDATE").contains(current)) {
                                        JOptionPane.showMessageDialog(new JFrame(), "The button: " + current + " cannot be called ADD, DELETE, QUERY or UPDATE; as a result compilation failed.", "Error", JOptionPane.ERROR_MESSAGE);
                                        error = 1;
                                        break;
                                    }
                                    set_Buttons = (set_Buttons + "public JButton " + current + "  = new JButton(\"" + current + "\");\n\t\t");
                                    add_Buttons = (add_Buttons + "Add_button(" + current + ", new " + Get_value(current) + "(this));\n\t\t");
                                    j++;
                                }

                                if (error == 1) {
                                    break;
                                }

                                if (!output.exists()) {
                                    output.getParentFile().mkdirs();
                                    output.createNewFile();
                                }
                                PrintWriter fileOut = new PrintWriter(new FileOutputStream(output));
                                Scanner fileIn = new Scanner(new FileInputStream("src/FrameWork.txt"));

                                while (fileIn.hasNext()) {
                                    the_Text = (the_Text + fileIn.nextLine() + "\n");
                                }
                                the_Text = the_Text.replaceAll("THEPROJNAM", Get_value("title"));
                                the_Text = the_Text.replaceAll("THEPROJECT", the_Project);
                                the_Text = the_Text.replaceAll("SETFIELDS", set_Fields);
                                the_Text = the_Text.replaceAll("SETBUTTON", set_Buttons);
                                the_Text = the_Text.replaceAll("ADDFIELDS", add_Fields);
                                the_Text = the_Text.replaceAll("ADDBUTTON", add_Buttons);
                                the_Text = the_Text.replaceAll("GETANDSET", getset_Methods);
                                the_Text = the_Text.replaceAll("NUMBER", Integer.toString(i));
                                the_Text = the_Text.replaceAll("BOOCHECK", boolean_Fields);
                                the_Text = the_Text.replaceAll("SQLENTRY", sql_Entry);
                                the_Text = the_Text.replaceAll("ACTIONADD", action_ADD);
                                the_Text = the_Text.replaceAll("ACTIONDEL", action_DELETE);
                                the_Text = the_Text.replaceAll("ACTIONQUE", action_QUERY);
                                the_Text = the_Text.replaceAll("ACTIONUPD", "");    /*Fix This If Time Permits*/
                                the_Text = the_Text.replaceAll("CONFIRM", confirm_String);
                                
                                fileOut.print(the_Text);
                                fileOut.close();
                                fileIn.close();

                                output = new File(working_Directory + "/" + the_Project + "/" + the_Project + "FieldEdit.java");
                                fileOut = new PrintWriter(new FileOutputStream(output));
                                fileOut.print("public interface " + the_Project + "FieldEdit{" + getset_Interface + "\npublic void appendToStatusArea(String message);\n}");
                                fileOut.close();

                                /*Move's the .jar only if needed*/
                                if (!new File(working_Directory + "/" + current_File.getName().replace(".config", "/mysql-connector-java-5.1.24-bin.jar")).exists()) {
                                    p = Runtime.getRuntime().exec("cp ./mysql-connector-java-5.1.24-bin.jar " + working_Directory + "/" + current_File.getName().replace(".config", "/mysql-connector-java-5.1.24-bin.jar"));
                                    p.waitFor();
                                }
                            } catch (Exception e) {
                                JOptionPane.showMessageDialog(new JFrame(), e, "Error", JOptionPane.ERROR_MESSAGE);
                            }
                            Reset_manager();
                            break;
                    }
                }
            }
        }
    }

    //I couldn't figure a way to prevent the attempted running if the file existed already, but wasn't overwritten in the compile step.
    private class Compile_run implements ActionListener {

        public void actionPerformed(ActionEvent ae) {
            JButton Click = new JButton();
            Click.addActionListener(new Compile_action());
            Click.doClick();
            File output = new File(working_Directory + "/" + current_File.getName().replace(".config", "") + "/" + current_File.getName().replace(".config", "") + ".java");
            if (output.exists() && saved == true) {
                try {
                    Process p = Runtime.getRuntime().exec(java_Compiler.trim() + " " + compile_Settings + " " + current_File.getName().replace(".config", "") + ".java", new String[0], new File(working_Directory + "/" + current_File.getName().replace(".config", "")));
                    BufferedReader is = new BufferedReader(new InputStreamReader(p.getErrorStream()));
                    String Line, Error = "";

                    while ((Line = is.readLine()) != null) {
                        Error = Error + Line + "\n";
                    }
                    if (Error.contains("cannot find symbol")) {
                        JOptionPane.showMessageDialog(new JFrame(), "The action listeners have not been set up.", "Error", JOptionPane.ERROR_MESSAGE);
                    } else if (Error.contains("")) {
                        ArrayList <String> run= new ArrayList<String>();
                        StringTokenizer args = new StringTokenizer(run_time_Options, "");
                     
                        run.add(java_run_Time);
                        while (args.hasMoreTokens())
                             run.add(args.nextToken());
                        run.add(current_File.getName().replace(".config",""));
                        
                        ProcessBuilder pb = new ProcessBuilder(run);
                        pb.directory(new File(working_Directory + "/" + current_File.getName().replace(".config","")));
			p = (pb.start());
                        p.waitFor();
                        
                        is = new BufferedReader(new InputStreamReader(p.getErrorStream()));
                        
                        Line = is.readLine();
                        if (!Line.equals(""))
                            JOptionPane.showMessageDialog(new JFrame(), "Something went wrong while trying to run, check your run time and run time options.", "Error", JOptionPane.ERROR_MESSAGE);
                        
                    } else {
                        JOptionPane.showMessageDialog(new JFrame(), "Something else went wrong.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception e) {
                }
            }
        }
    }

    private class Quit_action implements ActionListener {

        public void actionPerformed(ActionEvent ae) {
            if (Modified != false) {
                if (JOptionPane.showConfirmDialog(new JFrame(), "Do you wish to save the file before quitting?", "", JOptionPane.YES_NO_OPTION) == 0) {
                    JButton Click = new JButton();
                    Click.addActionListener(new Save_action());
                    Click.doClick();
                }
            }
            System.exit(0);
        }
    }

    private void Set_compiler() {
        final JFrame the_Frame = new JFrame();
        JPanel Buttons = new JPanel();
        JButton Ok = new JButton("Ok");
        JButton Cancel = new JButton("Cancel");
        JButton Browse = new JButton("Browse");
        final JTextField the_Field = new JTextField(java_Compiler);

        Ok.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                try {
                    java_Compiler = the_Field.getText();
                    java_Compiler = java_Compiler.trim();
                } catch (Exception e) {
                };
                the_Frame.dispose();
            }
        });
        Cancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                the_Frame.dispose();
            }
        });
        Browse.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                int return_Val = -1;
                JFileChooser open_Dialog = new JFileChooser();

                open_Dialog.setCurrentDirectory(new java.io.File("."));
                open_Dialog.setAcceptAllFileFilterUsed(true);
                open_Dialog.setFileHidingEnabled(true);
                return_Val = open_Dialog.showOpenDialog(null);

                if (return_Val == 0) {
                    the_Field.setText(open_Dialog.getSelectedFile().getPath());
                }
            }
        });
        Buttons.add(Ok);
        Buttons.add(Cancel);
        Buttons.add(Browse);

        the_Frame.setLocationRelativeTo(this);
        the_Frame.setTitle("Java Compiler");
        the_Frame.setSize(new Dimension(300, 90));
        the_Frame.setDefaultCloseOperation(HIDE_ON_CLOSE);
        the_Frame.add(new JLabel("Set the Java Compiler."), BorderLayout.NORTH);
        the_Frame.add(the_Field);
        the_Frame.add(Buttons, BorderLayout.SOUTH);
        the_Frame.setVisible(true);
    }

    private void Set_run() {
        final JFrame the_Frame = new JFrame();
        JPanel Buttons = new JPanel();
        JButton Ok = new JButton("Ok");
        JButton Cancel = new JButton("Cancel");
        JButton Browse = new JButton("Browse");
        final JTextField the_Field = new JTextField(java_run_Time);

        Ok.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                try {
                    java_run_Time = the_Field.getText();
                    java_run_Time = java_run_Time.trim();
                } catch (Exception e) {
                };
                the_Frame.dispose();
            }
        });
        Cancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                the_Frame.dispose();
            }
        });
        Browse.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                int return_Val = -1;
                JFileChooser open_Dialog = new JFileChooser();

                open_Dialog.setCurrentDirectory(new java.io.File("."));
                open_Dialog.setAcceptAllFileFilterUsed(true);
                open_Dialog.setFileHidingEnabled(true);
                return_Val = open_Dialog.showOpenDialog(null);

                if (return_Val == 0) {
                    the_Field.setText(open_Dialog.getSelectedFile().getPath());
                }
            }
        });

        Buttons.add(Ok);
        Buttons.add(Cancel);
        Buttons.add(Browse);

        the_Frame.setLocationRelativeTo(this);
        the_Frame.setTitle("Java Run-Time");
        the_Frame.setSize(new Dimension(300, 90));
        the_Frame.setDefaultCloseOperation(HIDE_ON_CLOSE);
        the_Frame.add(new JLabel("Set the Java Run-Time."), BorderLayout.NORTH);
        the_Frame.add(the_Field);
        the_Frame.add(Buttons, BorderLayout.SOUTH);
        the_Frame.setVisible(true);
    }

    private void Set_parser() {
        final JFrame the_Frame = new JFrame();
        JRadioButton[] radio_Button = new JRadioButton[2];
        JButton Ok = new JButton("Ok");
        ButtonGroup the_Group = new ButtonGroup();

        radio_Button[0] = new JRadioButton("Compile using the IDE");
        radio_Button[1] = new JRadioButton("Compile using LEX/YACC");

        the_Group.add(radio_Button[0]);
        the_Group.add(radio_Button[1]);

        if (IDE == false) {
            radio_Button[1].setSelected(true);
        } else {
            radio_Button[0].setSelected(true);
        }


        radio_Button[0].addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                IDE = true;
            }
        });
        radio_Button[1].addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                IDE = false;
            }
        });
        Ok.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                the_Frame.dispose();
            }
        });

        the_Frame.setLocationRelativeTo(this);
        the_Frame.setTitle("Java Run-Time");
        the_Frame.setSize(new Dimension(300, 90));
        the_Frame.setDefaultCloseOperation(HIDE_ON_CLOSE);
        the_Frame.setLayout(new GridLayout(4, 1));
        the_Frame.add(new JLabel("Set the Compile Mode."));
        the_Frame.add(radio_Button[0]);
        the_Frame.add(radio_Button[1]);
        the_Frame.add(Ok);
        the_Frame.setVisible(true);
    }
}
