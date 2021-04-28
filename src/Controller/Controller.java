/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
Bấm exit cho thoát luôn
bấm x bắt show lại message
 */
package Controller;

import GUI.FrogGame;
import KeyBroad.Key;
import java.awt.Color;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.geometry.VPos;
import javafx.scene.layout.GridPane;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.Timer;

/**
 *
 * @author dellpc
 */
public class Controller {

    private FrogGame f;//
    JButton btnPause, btnSave, btnExit;
    JLabel lblPoints;
    JPanel pnLayout;

    private Timer timer;//
    private List<JButton> list = new ArrayList<>();//
    int count = 0;
    int widthOfBtn = 50;
    int MAX_Count = 170;
//    Icon icon= new ImageIcon("frog.png");

    JButton btnF;
    int sizeOfFrog = 50;
    double yOfFrog = 70;
    int xOfFrog = 70;
    Key key = new Key();
    boolean checkSave = false;
    int mark = 0;
    boolean checkPause = false;
    ImageIcon imgIcon;
    URL url = getClass().getResource("/resources/ny.png");
    int time = 1;
    boolean isUpping = false;
    int controlUp = 0;
    double powerUp = 1.5;

//    private JButton btnF = new JButton();
//    private int yFrog = 60;
//    int score = 0;
    public Controller() {
        f = new FrogGame();

        imgIcon = new ImageIcon(url);
        Image img = imgIcon.getImage().getScaledInstance(sizeOfFrog, sizeOfFrog, java.awt.Image.SCALE_SMOOTH);
        imgIcon = new ImageIcon(img);
        btnF = new JButton(imgIcon);
//        btnF = new JButton();
        f.getPnLayout().add(btnF);
        btnF.addKeyListener(key);
        f.getBtnPause().addKeyListener(key);//Khi an Pause thì con cóc không được nghe sự kiện 
        //nên thêm vào như này để cả 2 cùng nghe sự kiện
        f.getBtnSave().addKeyListener(key);
        f.getBtnExit().addKeyListener(key);

        run();
        pressPause();
        pressSave();
        pressExit();

        f.setResizable(false);
        f.setLocationRelativeTo(null);
        f.setVisible(true);
        System.out.println(f.getPnLayout().getWidth());
        System.out.println(f.getPnLayout().getHeight());
    }

    //get mark when frog throught a pipe
    public void getMark() {
        for (JButton btn : list) {
            //frog through up and down button in list button
            if (btnF.getX() == btn.getX()) {
                mark++;
                f.getLblPoints().setText("Points: " + mark / 2);
            }
        }
    }

    //
    //get time upper each time
   

    //
    private void addPipes() {

        JButton btnUp = new JButton();
        JButton btnDown = new JButton();
        Random rd = new Random();

        int xOfBtn = f.getPnLayout().getWidth();
        int yOfBtnUp = 0;
        int heighOfBtnUp = rd.nextInt(120) + 70;//get random height of btn

        //
        btnUp.setBounds(xOfBtn, yOfBtnUp, widthOfBtn, heighOfBtnUp);

        int heighOfBtnDown = rd.nextInt(120) + 70;
        int yOfBtnDown = f.getPnLayout().getHeight() - heighOfBtnDown;

        btnDown.setBounds(xOfBtn, yOfBtnDown, widthOfBtn, heighOfBtnDown);

        f.getPnLayout().add(btnUp);
        f.getPnLayout().add(btnDown);
        list.add(btnUp);
        list.add(btnDown);
        btnUp.setBackground(Color.green);
        btnDown.setBackground(Color.green);

        btnDown.addKeyListener(key);
        btnUp.addKeyListener(key);
    }

    //
    public double getSpeed() {
        return 0.07 * time; //0.07 la gia toc trong truong ko doi-gravity
    }
    
    public void run() {
        timer = new Timer(15, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                time++;
                //press up button from key broad
                if (key.isPress()) {
                    isUpping = true;//
                    time = 1;
                    key.setPress(false);
                }
                //check conditions for frog up or down
                if (isUpping == false) {//frog down
                    yOfFrog = yOfFrog + getSpeed();//xuống 1 đoạn nhỏ, nhanh dần=y=y+gt
                } else {
                    //check count time for frog up
                    if (powerUp - getSpeed() <= 0) {//powerUp 
                        isUpping = false;
                        time=1;
                    }
                    yOfFrog = yOfFrog - (powerUp - getSpeed());//lên từ từ, lên mượt, chậm dần//y=y-()
                }
                //control frog can not through ceiling
                if (yOfFrog <= 0) {//Đâm lên tường cho nảy về 30
                    yOfFrog = 30;
                }

                btnF.setBounds(xOfFrog, (int) yOfFrog, sizeOfFrog, sizeOfFrog);

                //move all button of list buttons from right to left
                for (int i = 0; i < list.size(); i++) {
                    int x = list.get(i).getX() - 1;
                    int y = list.get(i).getY();

                    list.get(i).setLocation(x, y);

                    //if button through screen already, remove it
                    if (x <= -widthOfBtn) {
                        list.remove(i);
                        i--;
                    }
                }
                getMark();

                //add new button after each equal amount of time
                count++;
                if (count == MAX_Count) {//151
                    addPipes();//151
                    count = 0;
                }
                //check condition stop game
                if (checkTouch() == true) {
                    timer.stop();
                    showMes();
                }
                setMedal();
            }
        });
        timer.start();
    }

    public void setMedal() {
        if (mark >= 20 && mark < 40) {///>=10,<=20
            f.getLblMedal().setText("Broze Medal");
        } else if (mark >= 40 && mark < 60) {//>=20,<=30
            f.getLblMedal().setText("Silver Medal");
        } else if (mark >= 60 && mark < 80) {
            f.getLblMedal().setText("Gold Medal");
        } else if (mark >= 80) {
            f.getLblMedal().setText("Platinum Medal");
        } else {
            f.getLblMedal().setText("");
        }
    }

    //
    public boolean checkTouch() {
        //check frog is landfall
        if (btnF.getY() >= f.getPnLayout().getHeight() - sizeOfFrog - 2) {//vì sao-2
            return true;
        }
        Rectangle recFrog = new Rectangle(btnF.getX(), btnF.getY(),
                btnF.getWidth(), btnF.getHeight());
        //check touch list btn with frog
        for (JButton btn : list) {
            Rectangle recBtn = new Rectangle(btn.getX(), btn.getY(),
                    btn.getWidth(), btn.getHeight());
            //touched
            if (recFrog.intersects(recBtn)) {//????????????????????
                //set new heigh for frog if frog in range width of 
                //a button and it through that button
                if (btnF.getX() > btn.getX() - sizeOfFrog + 1
                        && btnF.getX() < btn.getX() + widthOfBtn - 1
                        && yOfFrog < btn.getHeight()) {
                    yOfFrog = btn.getHeight();
                    btnF.setBounds(xOfFrog, (int) yOfFrog, sizeOfFrog, sizeOfFrog);
                }
                return true;
            }
        }
        return false;
    }
//

    public void pressSave() {
        f.getBtnSave().addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                checkSave = true;
                try {
                    FileWriter fw = new FileWriter("data.txt");
                    BufferedWriter bw = new BufferedWriter(fw);

                    //Write coordinate and width, heighht 
                    //of all btn in list button to file
                    for (JButton btn : list) {
                        int x = btn.getX();
                        int y = btn.getY();
                        int width = btn.getWidth();
                        int height = btn.getHeight();
                        bw.write(x + ";" + y + ";" + width + ";" + height);
                        bw.newLine();
                    }
                    bw.write(count + ";" + mark + ";" + yOfFrog);//Count la cai gi?
                    bw.newLine();
                    bw.close();
                } catch (IOException ex) {
                    Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
    }

    private void pressPause() {
        f.getBtnPause().addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
//                checkPause = (checkPause == false);//pause=false mà đúng
//                if (checkPause == true) {
//                    f.getBtnPause().setText("Resume");//
//                    checkPause=false;
//                    timer.stop();
//                } else {
//                    f.getBtnPause().setText("Pause");
//                    timer.restart();
//                }
                    if(f.getBtnPause().getText().equals("Pause")){
                        f.getBtnPause().setText("Resume");
                        timer.stop();
                    }else{
                        f.getBtnPause().setText("Pause");
                        timer.restart();
                    }
            }
        });
    }

    private void pressExit() {
        f.getBtnExit().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
//                Object mes[] = {"Yes", "No"};
//                int option = JOptionPane.showOptionDialog(null, 
//                        "Do you want to exit game?",
//                        "Notice!",
//                        JOptionPane.YES_NO_CANCEL_OPTION,
//                        JOptionPane.QUESTION_MESSAGE,
//                        null, mes, mes[0]);
                timer.stop();
                int option = getOptionExit();
                //exit or not
                switch (option) {
                    //exit
                    case 0:
                        System.exit(0);
                        break;
                    case 1:
                        timer.restart();
                        break;
                }
            }
        });
    }
    //
    public int getOptionExit() {
        boolean notSelectClose = false;
        int option = -1;
        Object mes[] = {"Yes", "No"};

        //get Option until user do not select close button
        do {
            option = JOptionPane.showOptionDialog(f,
                    "Do you want to exit?",
                    "",
                    JOptionPane.YES_NO_CANCEL_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null, mes, mes[0]);

            //user select option
            if (!(option == -1)) {
                notSelectClose = true;
            }
        }while (notSelectClose == false);
        return option;
    }

    //getOption user with not close dialog in not save case
    public int getOptionNotSave() {

        boolean notSelectClose = false;
        int option = -1;
        Object mes[] = {"New Game", "Exit"};

        //get option until user do not select close button
       do {
            option = JOptionPane.showOptionDialog(f,
                    "Do you want to play a new game?",
                    "",
                    JOptionPane.YES_NO_CANCEL_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null, mes, mes[0]);
            if (!(option == -1)) { //user select option
                notSelectClose = true;
            }
        } while (notSelectClose = false);
        return option;
    }

    public int getOptionSave() {
        boolean notSelectClose = false;
        int option = -1;
        Object mes[] = {"New Game", "Continue", "Exit"};

        //get option until user do not select close button
        do {
            option = JOptionPane.showOptionDialog(f,
                    "Do you want to play a new game?",
                    "",
                    JOptionPane.YES_NO_CANCEL_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null, mes, mes[0]);
            if (!(option == -1)) { //user select option
                notSelectClose = true;
            }
        }while (notSelectClose = false);
        return option;
    }

    public void showMes() {
        if (checkSave == false) {
            //show mess and get option value by user
            int option = getOptionNotSave();

            switch (option) {
                case -1:
                    showMes();
                case 0:
                    isUpping = false;
                    controlUp = 0;
                    time = 1;
                    checkSave = false;
                    f.getPnLayout().removeAll();
                    f.getPnLayout().repaint();//???
                    list.clear();
                    mark = 0;
                    setMedal();
                    count = 150;
                    f.getLblPoints().setText("Points: 0");
                    yOfFrog = 50;
                    f.getPnLayout().add(btnF);
//                    btnF.setSelected(true);//??
                    timer.restart();
                    break;

                //exit game
                case 1:
                    System.exit(0);
                    break;
                

            }

        } else {
            //show mess and get option value by user
            int option = getOptionSave();

            switch (option) {
                case 0:
                    isUpping = false;
                    controlUp = 0;
                    time = 1;
                    checkSave = false;
                    f.getPnLayout().removeAll();
                    f.getPnLayout().repaint();//?????
                    list.clear();
                    mark = 0;
                    setMedal();
                    count = 150;
                    f.getLblPoints().setText("Points: 0");
                    yOfFrog = 50;
                    f.getPnLayout().add(btnF);
//                    btnF.setSelected(true);//??
                    timer.restart();
                    break;
                case 1:
                    isUpping = false;
                    controlUp = 0;
                    time = 1;
                    f.getPnLayout().removeAll();
                    f.getPnLayout().repaint();//?????
                    list.clear();
                    setMedal();

                    try {
                        FileReader fr = new FileReader("data.txt");
                        BufferedReader br = new BufferedReader(fr);
                        String line = "";
                        //set position of pipes and frog at save time
                        do {
                            line = br.readLine().trim();
                            if (line == null) {
                                break;
                            }
                            JButton btn = new JButton();
                            String txt[] = line.split(";");
                            double arr[] = new double[txt.length];

                            //get number in a line from file data to array int
                            for (int i = 0; i < arr.length; i++) {
                                arr[i] = Double.parseDouble(txt[i]);
                            }
                            

                            //if this line store position and size of button
                            if (txt.length == 4) {
                                btn.setBounds((int) arr[0], (int) arr[1], (int) arr[2], (int) arr[3]);
                                list.add(btn);
                                f.getPnLayout().add(btn);
                                btn.addKeyListener(key);

                                //if line text store count, mark, y of frog value
                            } else {
                                count = (int) arr[0];//
                                mark = (int) arr[1];
                                f.getLblPoints().setText("Points: " + mark / 2);
                                yOfFrog = arr[2];
                            }

                        } while (true);
                        br.close();
                    } catch (Exception e) {
                        System.out.println(e);
                    }
                    f.getPnLayout().add(btnF);//lieen quan gi y
                    timer.restart(); //
                    break;
                //exit
                case 2:
                    System.exit(0);
                    break;
            }
        }
    }

}
