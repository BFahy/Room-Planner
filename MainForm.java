package Room_Planner;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.LinkedList;
import java.util.Random;

import static java.lang.Integer.parseInt;

public class MainForm extends JFrame implements ActionListener {
    Color currentColor = Color.WHITE;
    SpringLayout myLayout = new SpringLayout();
    // Sets room array size, change 11 and 19 to adjust X and Y
    JTextField[][] roomArray = new JTextField[11][19];
    JButton btnFurnitureCount, btnExit, btnClear, btnSave, btnLoad, btnFind,
            btnOrange, btnCyan, btnGreen,
            btnYellow, btnMagenta, btnWhite;

    JLabel lblClient, lblSite, lblRoom, lblDate, lblColorSelect;
    JTextField txtClient, txtSite, txtRoom, txtDate, txtFind;

    String filepath;

    /**
     * MainForm containing methods to be run when application form is initialized and displayed to client
     */
    public MainForm()
    {
        // Set size of form
        setSize(770,530);
        // Set location on open
        setLocation(400,100);
        setLayout(myLayout);

        // Builds room planner text fields, buttons and labels
        BuildRoomInformation();
        BuildTextField();

        // Closes application on X press
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
                System.exit(0);
            }
        });


        setVisible(true);
    }

    /**
     * Used to build 2D array of text fields for displaying room data
     */
    public void BuildTextField()
    {
        for (int x = 0; x < roomArray.length; x++) {
            for (int y = 0; y < roomArray[x].length; y++) {
                int posX = 40 + x * 55;
                int posY = 50 + y * 20;
                roomArray[x][y] = UIBuilder.CreateATextField(5, posX, posY, myLayout, this);

                roomArray[x][y].addFocusListener(new FocusAdapter() {
                    @Override
                    public void focusLost(FocusEvent e) {
                        super.focusLost(e);
                        JTextField focus = (JTextField) e.getSource();
                        if (!focus.getText().isEmpty())
                        {
                            focus.setBackground(currentColor);
                        }
                    }
                });
            }
        }
    }

    /**
     * Method allows user to select what folder to set location of file when it is saved, allows for .RAF, .csv
     * Will automatically save .dat for vacuum cleaner information regardless of .RAF or .csv choice.
     */
    public void ChooseWritePath()
    {
        FileDialog dialog = new FileDialog(this, "Choose Save Location", FileDialog.SAVE);
        // Sets default file name
        dialog.setFile("default");
        dialog.setVisible(true);
        if (dialog.getFile() == null)
        {
            return;
        }
        filepath = dialog.getDirectory() + dialog.getFile();
        if (filepath.endsWith(".RAF"))
        {
            WriteToRAF();
            filepath = filepath.replace(".RAF", "");
            WriteVacuumData();
        }
        else if (filepath.endsWith(".csv"))
        {
            WriteToFile();
            // Replaces .csv for use by vacuum data (prevents .csv.dat)
            filepath = filepath.replace(".csv", "");
            WriteVacuumData();
        } else
        {
            // If no file extension, saves as .dat before defaulting to .csv save
            WriteVacuumData();
            filepath += ".csv";
            WriteToFile();
        }
    }

    /**
     * Method allows user to select what folder to load file from, allows for .RAF or .csv
     */
    public void ChooseReadPath()
    {
        FileDialog dialog = new FileDialog(this, "Choose File to Load", FileDialog.LOAD);
        dialog.setVisible(true);
        if (dialog.getFile() == null)
        {
            return;
        }
        filepath = dialog.getDirectory() + dialog.getFile();
        // Resets room plan before loading to prevent overlapping
        ClearPlan();
        // If statements to check for appropriate file extensions
        if (filepath.endsWith(".csv"))
        {
            ReadFromFile(filepath);
        }
        else if (filepath.endsWith(".RAF"))
        {
            ReadFromRAF(filepath);
        }

    }

    /**
     * Method called once filepath is chosen in ChooseReadPath(), for .csv file
     * Used to iterate through a .csv file and return the contents for display in room plan
     * @param filepath given once .csv file is selected to be loaded
     */
    public void ReadFromFile(String filepath)
    {
        try
        {
            BufferedReader read = new BufferedReader(new FileReader(filepath));
            for (int i = 0; i < 110; i++) {
                // Allows for reading each client, site, room and date entry and assign to text field
                String[] temp = read.readLine().split(",");
                if (temp[0].equalsIgnoreCase("Client:"))
                {
                    txtClient.setText(temp[1]);
                    continue;
                }
                if (temp[0].equalsIgnoreCase("Site:"))
                {
                    txtSite.setText(temp[1]);
                    continue;
                }
                if (temp[0].equalsIgnoreCase("Room:"))
                {
                    txtRoom.setText(temp[1]);
                    continue;
                }
                if (temp[0].equalsIgnoreCase("Date:"))
                {
                    txtDate.setText(temp[1]);
                    continue;
                }

                for (int j = 0; j < 10; j++) {
                    // For setting room array location and text
                    roomArray[parseInt(temp[0])][parseInt(temp[1])].setText(temp[2]);
                    // For reading text of colour and assigning as correct color format
                    if (temp[3].equalsIgnoreCase("Green"))
                    {
                        roomArray[parseInt(temp[0])][parseInt(temp[1])].setBackground(Color.GREEN);
                    }
                    if (temp[3].equalsIgnoreCase("Yellow"))
                    {
                        roomArray[parseInt(temp[0])][parseInt(temp[1])].setBackground(Color.YELLOW);
                    }
                    if (temp[3].equalsIgnoreCase("Purple"))
                    {
                        roomArray[parseInt(temp[0])][parseInt(temp[1])].setBackground(Color.MAGENTA);
                    }
                    if (temp[3].equalsIgnoreCase("Blue"))
                    {
                        roomArray[parseInt(temp[0])][parseInt(temp[1])].setBackground(Color.CYAN);
                    }
                    if (temp[3].equalsIgnoreCase("Orange"))
                    {
                        roomArray[parseInt(temp[0])][parseInt(temp[1])].setBackground(Color.ORANGE);
                    }
                    if (temp[3].equalsIgnoreCase("Pink"))
                    {
                        roomArray[parseInt(temp[0])][parseInt(temp[1])].setBackground(Color.PINK);
                    }
                }
            }
            read.close();
        }
        catch(Exception e)
        {
            System.err.println("Error with file: " + e.getMessage());
        }
    }

    /**
     * Method called once filename is entered in ChooseWritePath(), by default or with .csv file extension
     * Used to iterate through a .csv file and save the contents of room plan
     */
    public void WriteToFile()
    {
        // Creates initial string containing client, site, room and date to add to beginning of file when saved
        ColumnData data = new ColumnData(txtClient.getText(), txtSite.getText(), txtRoom.getText(), txtDate.getText());
        String clientInfo = "Client:," + data.getPlanClient() + "\nSite:," + data.getPlanSite() + "\nRoom:," + data.getPlanRoom()
                + "\nDate:," + data.getPlanDate();

        try {
            BufferedWriter write = new BufferedWriter(new FileWriter(filepath));
            write.write(clientInfo);
            write.newLine();
            for (int i = 0; i < 11; i++) {
                String saveString = "";
                for (int j = 0; j < 19; j++) {
                    // Checks if text is empty to prevent saving empty strings & positions
                    if (!roomArray[i][j].getText().equals("")) {
                        // Saves position x, y and text inside
                        saveString += i + "," + j + "," + roomArray[i][j].getText() + ",";
                        // Adds colour in plain text - Color.GREEN saves as "Green"
                        saveString = colorCheck(i, saveString, j);
                        // Adds Yes/No depending if item is fixed or not
                        if (roomArray[i][j].getText().equalsIgnoreCase("Desk") || roomArray[i][j].getText().equalsIgnoreCase("Glass Door")
                        || roomArray[i][j].getText().equalsIgnoreCase("Cabinet") || roomArray[i][j].getText().equalsIgnoreCase("Window")
                        || roomArray[i][j].getText().equalsIgnoreCase("Bookshelf") || roomArray[i][j].getText().equalsIgnoreCase("TV Cabinet")
                        || roomArray[i][j].getText().equalsIgnoreCase("Table") || roomArray[i][j].getText().equalsIgnoreCase("Door"))
                        {
                            saveString += "Yes\n";
                        }
                        else
                        {
                            saveString += "No\n";
                        }
                    }
                }
                write.write(saveString);
            }
            write.close();
        }
        catch(Exception e)
        {
            System.out.println("Error Writing to File: " + e.getMessage());
        }
    }


    /**
     * Method called once filename is entered in ChooseWritePath() when .RAF file extension is added
     * Used to create a Random Access FIle containing contents of room plan
     */
    public void WriteToRAF()
    {
        try
        {
            RandomAccessFile raf = new RandomAccessFile(filepath, "rw");
            raf.seek(20);
            raf.writeUTF(txtClient.getText());
            raf.seek(40);
            raf.writeUTF(txtDate.getText());
            raf.seek(60);
            raf.writeUTF(txtRoom.getText());
            raf.seek(80);
            raf.writeUTF(txtSite.getText());
            int count = 100;
            for (int i = 0; i < 11; i++)
            {
                for (int j = 0; j < 19; j++)
                {
                    int value = roomArray[i][j].getBackground().getRGB();
                    String entry = roomArray[i][j].getText();
                    raf.seek(count * 40);
                    raf.writeUTF(Integer.toString(value));
                    raf.writeUTF(entry);
                    count++;
                }
            }
            raf.close();
        }
        catch (Exception e)
        {
            System.out.println("Error Writing to RAF: " + e.getMessage());
        }
    }


    /**
     * Method called once filepath is chosen in ChooseReadPath() for .RAF file
     * Used to load from .RAF file and display the contents for display in room plan
     * @param filepath given once .RAF file is selected to be loaded
     */
    public void ReadFromRAF(String filepath)
    {
        try
        {
            RandomAccessFile raf = new RandomAccessFile(filepath, "r");
            raf.seek(20);
            txtClient.setText(raf.readUTF());
            raf.seek(40);
            txtDate.setText(raf.readUTF());
            raf.seek(60);
            txtRoom.setText(raf.readUTF());
            raf.seek(80);
            txtSite.setText(raf.readUTF());
            int count = 100;
            for (int i = 0; i < 11; i++)
            {
                for (int j = 0; j < 19; j++)
                {
                    raf.seek(count * 40);
                    roomArray[i][j].setBackground(new Color(Integer.parseInt(raf.readUTF())));
                    roomArray[i][j].setText(raf.readUTF());
                    count++;
                }
            }

            raf.close();
        }
        catch (Exception e)
        {
            System.out.println("Error reading from RAF: " + e.getMessage());
        }
    }



    /**
     * Writes to a plain file, position x and y and yes or no depending on if it is fixed
     * Used by vacuum to check if furniture piece is fixed in place
     */
    public void WriteVacuumData()
    {
        try {
            // Adds VacuumData to file
            BufferedWriter write = new BufferedWriter(new FileWriter(filepath + "VacuumData"));
            for (int i = 0; i < 11; i++) {
                String saveString = "";
                for (int j = 0; j < 19; j++) {
                    if (!roomArray[i][j].getText().equals("")) {
                        // Saves as X, Y and yes/no
                        saveString += i + "," + j + ",";
                        //saveString = colorCheck(i, saveString, j);
                        if (roomArray[i][j].getText().equalsIgnoreCase("Desk") || roomArray[i][j].getText().equalsIgnoreCase("Glass Door")
                                || roomArray[i][j].getText().equalsIgnoreCase("Cabinet") || roomArray[i][j].getText().equalsIgnoreCase("Window")
                                || roomArray[i][j].getText().equalsIgnoreCase("Bookshelf") || roomArray[i][j].getText().equalsIgnoreCase("TV Cabinet")
                                || roomArray[i][j].getText().equalsIgnoreCase("Table") || roomArray[i][j].getText().equalsIgnoreCase("Door"))
                        {
                            saveString += "Yes\n";
                        }
                        else
                        {
                            saveString += "No\n";
                        }
                    }
                }
                write.write(saveString);
            }
            write.close();
        }
        catch(Exception e)
        {
            System.out.println("Error Writing to File: " + e.getMessage());
        }
    }

    /**
     * Helper method for WriteToFile background check when writing
     */
    private String colorCheck(int i, String saveString, int j) {
        if (roomArray[i][j].getBackground().getRGB() == Color.YELLOW.getRGB())
        {
            saveString += "Yellow,";
        }
        if (roomArray[i][j].getBackground().getRGB() == Color.GREEN.getRGB())
        {
            saveString += "Green,";
        }
        if (roomArray[i][j].getBackground().getRGB() == Color.CYAN.getRGB())
        {
            saveString += "Blue,";
        }
        if (roomArray[i][j].getBackground().getRGB() == Color.MAGENTA.getRGB())
        {
            saveString += "Purple,";
        }
        if (roomArray[i][j].getBackground().getRGB() == Color.ORANGE.getRGB())
        {
            saveString += "Orange,";
        }
        if (roomArray[i][j].getBackground().getRGB() == Color.WHITE.getRGB())
        {
            saveString += "White,";
        }
        if (roomArray[i][j].getBackground().getRGB() == Color.PINK.getRGB())
        {
            saveString += "Pink,";
        }
        return saveString;
    }

    /**
     * Method containing information on reacting to when a button is pressed such as clear, exit, save etc
     * @param e Related to the on-press ActionEvent
     */
    @Override
    public void actionPerformed(ActionEvent e) {

        // When exit button is pressed, close application
        if (e.getSource() == btnExit)
        {
            System.exit(0);
        }

        // When furniture count button is pressed, open CountForm() and display sorted/counted items
        if (e.getSource() == btnFurnitureCount)
        {
            LinkedList<FurnitureData> furnitureList = new LinkedList<FurnitureData>();
            for (int i = 0; i < roomArray.length; i++)
            {
                for (int j = 0; j < roomArray[0].length; j++)
                {
                    int position = IsContainedInList(roomArray[i][j].getText(), furnitureList);
                    if (position == -1)
                    {
                        if(!roomArray[i][j].getText().isEmpty())
                        {
                            furnitureList.add(new FurnitureData(roomArray[i][j].getText(), 1));
                        }
                    }
                    else
                    {
                        furnitureList.get(position).incrementCount();
                    }
                }
            }
            new CountForm(furnitureList);
        }

        // Clear text and background colour of 2D roomArray on display
        if(e.getSource() == btnClear)
        {
            ClearPlan();
        }

        // Open file directory to find .csv or .raf file
        if (e.getSource() == btnLoad)
        {
            ChooseReadPath();
        }

        // Choose file location to save .csv or .RAF file + .DAT for vacuum cleaner
        if(e.getSource() == btnSave)
        {
            ChooseWritePath();
        }

        // Find and change colour of item entered in search text box
        if(e.getSource() == btnFind)
        {
            for (int i = 0; i < roomArray.length; i++)
            {
                for (int j = 0; j < roomArray[i].length; j++)
                {
                    if(roomArray[i][j].getText().equalsIgnoreCase(txtFind.getText()) && !txtFind.getText().equals(""))
                    {
                        roomArray[i][j].setBackground(Color.PINK);
                    }
                }
            }
        }

        // Used when colour button is pressed, remove border of other colours to set highlight of selected colour
        if (e.getSource() == btnOrange)
        {
            currentColor = Color.ORANGE;
            btnOrange.setBorderPainted(true);
            btnCyan.setBorderPainted(false);
            btnGreen.setBorderPainted(false);
            btnYellow.setBorderPainted(false);
            btnMagenta.setBorderPainted(false);
        }
        if (e.getSource() == btnCyan)
        {
            currentColor = Color.cyan;
            btnOrange.setBorderPainted(false);
            btnCyan.setBorderPainted(true);
            btnGreen.setBorderPainted(false);
            btnYellow.setBorderPainted(false);
            btnMagenta.setBorderPainted(false);
        }
        if (e.getSource() == btnGreen)
        {
            currentColor = Color.GREEN;
            btnOrange.setBorderPainted(false);
            btnCyan.setBorderPainted(false);
            btnGreen.setBorderPainted(true);
            btnYellow.setBorderPainted(false);
            btnMagenta.setBorderPainted(false);
        }
        if (e.getSource() == btnYellow)
        {
            currentColor = Color.YELLOW;
            btnOrange.setBorderPainted(false);
            btnCyan.setBorderPainted(false);
            btnGreen.setBorderPainted(false);
            btnYellow.setBorderPainted(true);
            btnMagenta.setBorderPainted(false);
        }
        if (e.getSource() == btnMagenta)
        {
            currentColor = Color.MAGENTA;
            btnOrange.setBorderPainted(false);
            btnCyan.setBorderPainted(false);
            btnGreen.setBorderPainted(false);
            btnYellow.setBorderPainted(false);
            btnMagenta.setBorderPainted(true);
        }
        if (e.getSource() == btnWhite)
        {
            currentColor = Color.WHITE;
            btnOrange.setBorderPainted(false);
            btnCyan.setBorderPainted(false);
            btnGreen.setBorderPainted(false);
            btnYellow.setBorderPainted(false);
            btnMagenta.setBorderPainted(false);
        }
    }

    /**
     * Method used for clearing 2D array textFields and resetting room plan.
     */
    private void ClearPlan()
    {
        txtClient.setText("");
        txtDate.setText("");
        txtRoom.setText("");
        txtSite.setText("");
        // Iterates through room plan to set background to white and blank text
        for (int x = 0; x < roomArray.length; x++)
        {
            for (int y = 0; y < roomArray[x].length; y++)
            {
                roomArray[x][y].setBackground(Color.WHITE);
                roomArray[x][y].setText("");
            }
        }
    }

    /**
     * Helper method containing room plan labels and buttons associated
     */
    private void BuildRoomInformation()
    {
        // Bottom buttons
        btnFurnitureCount = UIBuilder.CreateAButton("Furniture Count", 125, 25, 35, 460, this, myLayout, this);
        btnExit = UIBuilder.CreateAButton("Exit", 60, 25, 640, 460, this, myLayout, this);
        btnClear = UIBuilder.CreateAButton("Clear", 70, 25, 170, 460, this, myLayout, this);
        btnSave = UIBuilder.CreateAButton("Save", 70, 25, 250, 460, this, myLayout, this);
        btnLoad = UIBuilder.CreateAButton("Load", 70, 25, 328, 460, this, myLayout, this);

        btnFind = UIBuilder.CreateAButton("Find", 70, 25, 410, 460, this, myLayout, this);
        txtFind = UIBuilder.CreateATextField(8, 480, 463, myLayout, this);

        // Top labels & fields
        lblClient = UIBuilder.CreateALabel("Client: ", 50, 20, myLayout, this);
        txtClient = UIBuilder.CreateATextField(8, 88, 20, myLayout, this);

        lblSite = UIBuilder.CreateALabel("Site:", 205, 20, myLayout, this);
        txtSite = UIBuilder.CreateATextField(8, 238, 20, myLayout, this);

        lblRoom = UIBuilder.CreateALabel("Room:", 350, 20, myLayout, this);
        txtRoom = UIBuilder.CreateATextField(8, 388, 20, myLayout, this);

        lblDate = UIBuilder.CreateALabel("Date:", 500, 20, myLayout, this);
        txtDate = UIBuilder.CreateATextField(8, 538, 20, myLayout, this);

        // Colour select label and buttons
        lblColorSelect = UIBuilder.CreateALabel("Select Colour", 655, 120, myLayout, this);

        btnOrange = UIBuilder.CreateAButton("", 50, 17, 670, 147, this, myLayout, this);
        btnOrange.setBackground(Color.ORANGE);

        btnCyan = UIBuilder.CreateAButton("", 50, 17, 670, 167, this, myLayout, this);
        btnCyan.setBackground(Color.CYAN);

        btnGreen = UIBuilder.CreateAButton("", 50, 17, 670, 187, this, myLayout, this);
        btnGreen.setBackground(Color.GREEN);

        btnYellow = UIBuilder.CreateAButton("", 50, 17, 670, 207, this, myLayout, this);
        btnYellow.setBackground(Color.YELLOW);

        btnMagenta = UIBuilder.CreateAButton("", 50, 17, 670, 227, this, myLayout, this);
        btnMagenta.setBackground(Color.MAGENTA);

        btnWhite = UIBuilder.CreateAButton("", 50, 17, 670, 247, this, myLayout, this);
        btnWhite.setBackground(Color.WHITE);

        btnOrange.setBorderPainted(false);
        btnCyan.setBorderPainted(false);
        btnGreen.setBorderPainted(false);
        btnYellow.setBorderPainted(false);
        btnMagenta.setBorderPainted(false);

    }

    /**
     * Helper method for checking an item in a list, helper method to sorting & counting the totals.
     * @param furniture Item being counted
     * @param list List associated to check if it is already within the list
     * @return Returns items index or returns a -1 if the search has failed or is empty
     */
    private int IsContainedInList(String furniture, LinkedList<FurnitureData> list)
    {
        if (list.size() == 0)
        {
            return -1;
        }
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getFurniture().equalsIgnoreCase(furniture))
            {
                return i;
            }
        }
        return -1;
    }

}
