/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package MultiProg;

import java.awt.Dimension;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Scanner;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.SwingWorker;
import jssc.SerialPort;
import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;
import jssc.SerialPortException;
import jssc.SerialPortList;

/**
 *
 * @author piotr
 */
public class CommonThread extends Thread{
   String  Mod_list[]= new String [50];
   String Ver_list[] = new String [20];
   String _Description;
   JComboBox module_version;
   JModuleBox module_list;
   Database ropam_base;
   JTextArea description;
   JTextArea log_area;
   JButton program_button;
   ListenerTwo ver = new ListenerTwo() ;
   ListenerOne list= new ListenerOne();
   ListenerThree prog_button = new ListenerThree();
   ListenerFour log_listener = new ListenerFour();
   ListenerFive terminal_listener = new ListenerFive();
   PortListener portlistener = new PortListener();
   boolean _visible = false;
   JLabel status_label;
   Window window;
   Dimension window_size =new Dimension(410,700);   
   Dimension window_default_size =new Dimension(410,380);
   JCheckBox check_log;
   File config_file= new File("config.cfg");
   String path_name;
   Scanner read_scanner;
   //Dimension windows_size = new Dimension(400,370);  
   Thread prog_thread;             
  JLabel prog_label;         
   Icon icon = new ImageIcon("Pics/waiting.gif");
   JLabel animation = new JLabel(icon);
   JButton terminal;
   JFrame terminal_window;
   JModuleBox ports;
   JButton refresh_ports;
   JButton connect_to_port;
    JTextArea data_port;
   LayoutManager Terminal_Layout = null;
   Dimension terminal_size = new Dimension(410,400);
   SerialPort serial_port;
//   SerialPortReader port_reader;       
   
   
   
   
   
    @Override
    public void run(){
    window = new Window();
    window.setResizable(false);
    module_list = new JModuleBox();
    module_version = new JComboBox();
    ports= new JModuleBox();
    
    prog_label = new JLabel("Programowanie...");
    prog_label.setBounds(190,315,250,20);
    prog_label.setVisible(false);
    
    description = new JTextArea();
    description.setBounds(10, 100, 380, 205);
    description.setWrapStyleWord(true);
    description.setLineWrap(true);
    
    terminal = new JButton("Terminal");
    terminal.setBounds(10,315,100,20);
    terminal.addActionListener(terminal_listener);
    
    window.add(terminal);
    log_area = new JTextArea();
    log_area.setBounds(10, 360, 350, 300);
    log_area.setEditable(false);
    log_area.setWrapStyleWord(true);
    log_area.setLineWrap(true);
    
    
    animation.setBounds(220,300,250,50);
    window.add(animation);
    animation.setVisible(false);
    //prog_label.setBounds(100,310,250,20);        
    
    module_version.setBounds(170, 50, 80, 30);
    module_list.setBounds(10, 50, 150, 30);
    module_version.addActionListener(ver);
    module_list.addActionListener(list);
    
    description.setEditable(false);
    description.setWrapStyleWord(true);
        try {
           read_scanner= new Scanner(config_file);
        } catch (FileNotFoundException ex) {
            JOptionPane.showMessageDialog(null, "Nie znaleziono pliku konfiguracyjnego!");
            Logger.getLogger(CommonThread.class.getName()).log(Level.SEVERE, null, ex);
        }
    path_name = read_scanner.next();
    
    
    program_button = new JButton("Zaprogramuj");
    program_button.setBounds(260, 50, 120, 30);
    
  program_button.addActionListener(prog_button);
    
    window.add(module_list);
    window.add(description);
    window.add(module_version);
    window.add(program_button);
    window.add(log_area);
    window.add(prog_label);
    
    ropam_base= new Database("org.sqlite.JDBC");
    ropam_base.openbase(path_name);
    
    Mod_list=ropam_base.ReadAllModules();
    module_list.WriteModuleList(Mod_list);
    module_version.setEnabled(false);
    
    check_log=new JCheckBox("Log");
    check_log.setBounds(120,315,50,20);
    check_log.addActionListener(log_listener);
    window.add(check_log);
    window.repaint();
   
    terminal_window = new JFrame("Terminal");
    terminal_window.setSize(400, 400);
    
   terminal_window.pack();
     terminal_window.setLayout(Terminal_Layout);
    terminal_window.setSize(terminal_size);
    terminal_window.setResizable(false);
    data_port = new JTextArea();
    terminal_window.add(data_port);
    data_port.setVisible(true);
    data_port.setBounds(10,20,100,100);
//    port_reader = new SerialPortReader();
//       try {
//           serial_port.addEventListener(port_reader);
//       } catch (SerialPortException ex) {
//           Logger.getLogger(CommonThread.class.getName()).log(Level.SEVERE, null, ex);
//       }
}


class ListenerTwo implements ActionListener {    //listener od wersji
     @Override
    public void actionPerformed(ActionEvent event) {
         String Desc_tab;
       description.setText("");
        String _selModule = module_list.GetSelectModule();
         String _selVersion=GetSelectVersion();
       Desc_tab = ropam_base.ReadDescription(_selModule,_selVersion);
    description.setText(Desc_tab);
    }
}    

class ListenerFive implements ActionListener {    //listener od terminalu
     @Override
   
    public void actionPerformed(ActionEvent event) {
         
         String [] com_ports;
         ports.removeActionListener(portlistener);
         com_ports =SerialPortList.getPortNames();
           terminal_window.setVisible(true);
        terminal_window.add(ports);
        ports.removeAllItems();
    ports.setBounds(10,10,180,20);
    ports.setVisible(true);
    for(int a=0;a<com_ports.length;a++){
        ports.addItem(com_ports[a]);
    }
    terminal_window.repaint();
       ports.addActionListener(portlistener);
    }
     
}

  class ListenerOne implements ActionListener {   //lisener od nazw modulow
      @Override
    
    public void actionPerformed(ActionEvent event) {
        
          module_version.removeActionListener(ver);          
          description.setText("");
        module_version.removeAllItems();
    String _setModule=module_list.GetSelectModule();
    Ver_list=ropam_base.ReadVersion(_setModule);
    for(int a=0;a<Ver_list.length;a++){
        module_version.addItem(Ver_list[a]);
    }
    module_version.addActionListener(ver);
    module_version.setEnabled(true);
    
    String _selModule = module_list.GetSelectModule();
         String _selVersion=GetSelectVersion();
    String Desc_tab = ropam_base.ReadDescription(_selModule,_selVersion);
    description.setText(Desc_tab);
    
}
 
  
  }   
 class ListenerFour implements ActionListener {    //listener od wersji
     @Override
    public void actionPerformed(ActionEvent event) {
      if(check_log.isSelected()){
      window.SetSize(window_size);
      window.repaint();
             
    }
      if(!(check_log.isSelected())){
      window.SetSize(window_default_size);
      window.repaint();
             
    }
  }
}  
 
  class ListenerThree implements ActionListener {    //listener od przycisku programowania 
     @Override
    public void actionPerformed(ActionEvent event) {
         program_button.setEnabled(false);
         module_list.setEnabled(false);
         module_version.setEnabled(false);
         prog_label.setVisible(true);
         animation.setVisible(true);
        SwingWorker work= new SwingWorker() {
            
            @Override
            protected Object doInBackground() throws Exception {
                byte Flash[]=ropam_base.ReadFlash(module_list.GetSelectModule(), GetSelectVersion());// JOptionPane.showMessageDialog(null, "przycisk ok!");
         byte Eeprom[]=ropam_base.ReadEeprom(module_list.GetSelectModule(), GetSelectVersion());
         String Script[] = ropam_base.ReadScript(module_list.GetSelectModule(), GetSelectVersion());
         File flash_file = new File("Script/Flash");
       File eeprom_file = new File("Script/Eeprom");
       String line;
       String line2;
       InputStream is ;         
       InputStream is2 ;
        BufferedReader br2 = null;
       log_area.setText("");
     StringBuilder log_builder=new StringBuilder();
          try {
              System.out.flush();
              FileOutputStream Flash_Out = new FileOutputStream(flash_file);
              FileOutputStream EepromOut = new FileOutputStream(eeprom_file);
            try {
                Flash_Out.write(Flash);
                Flash_Out.close();
                EepromOut.write(Eeprom);
                EepromOut.close();
                
                int first_script = 1;
                int second_script =1;
               
              
                Runtime runtime = Runtime.getRuntime();
                String path=flash_file.getParent();
                String command = (path+'/'+ Script[0]);
                
                Process exec = runtime.exec(command);                   
                is = exec.getInputStream();
                InputStreamReader isr = new InputStreamReader(is);   //przejecie strumienia
                BufferedReader br = new BufferedReader(isr);                                   
                  int waitFor = exec.waitFor();
                  first_script = exec.exitValue();  
                  
                  if (Script.length > 1){
                 String command2 = (path+'/'+ Script[1]);
                Process exec2 = runtime.exec(command2);
                 is2 = exec2.getInputStream();
                InputStreamReader isr2 = new InputStreamReader(is2);   //przejecie strumienia
                 br2 = new BufferedReader(isr2);
                  int waitFor2 = exec2.waitFor();
                  int res2 = exec.exitValue();
//                  System.out.println(exec.exitValue());
//                  System.out.println(exec2.exitValue());
                  
                  second_script = exec2.exitValue();
                  }else{  
                  second_script = 0;
                  }
                 program_button.setEnabled(true);
                   module_list.setEnabled(true);
         module_version.setEnabled(true);
         prog_label.setVisible(false);
         animation.setVisible(false);
                  if ((first_script ==0)&(second_script == 0) ){
                    JOptionPane.showMessageDialog(null,"Programowanie OK!","OK!",JOptionPane.INFORMATION_MESSAGE);
                 
                  }else
                  {
                      JOptionPane.showMessageDialog(null,"Błąd w trakcie programowania!","Błąd!",JOptionPane.ERROR_MESSAGE);
                 
                  }
                 
                  flash_file.delete();
                  eeprom_file.delete();
                 
                  while ((line = br.readLine()) != null) {   //wywalenie strumienia do wyjscia
                       log_builder.append(line);
                       
                             }
                   while ((line2 = br2.readLine()) != null) {   //wywalenie strumienia do wyjscia
                       log_builder.append(line2);
                       
                             }
                   
                  log_area.setText(log_builder.toString());
                  
            } catch (IOException ex) {
                program_button.setEnabled(true);
                   module_list.setEnabled(true);
         module_version.setEnabled(true);
         prog_label.setVisible(false);
         animation.setVisible(false);
                Logger.getLogger(CommonThread.class.getName()).log(Level.SEVERE, null, ex);
            } catch (InterruptedException ex) { 
                program_button.setEnabled(true);
                   module_list.setEnabled(true);
         module_version.setEnabled(true);
         prog_label.setVisible(false);
         animation.setVisible(false);
                 Logger.getLogger(CommonThread.class.getName()).log(Level.SEVERE, null, ex);
             } 
          } catch (FileNotFoundException ex) {
              program_button.setEnabled(true);
                   module_list.setEnabled(true);
         module_version.setEnabled(true);
         prog_label.setVisible(false);
         animation.setVisible(false);
              Logger.getLogger(CommonThread.class.getName()).log(Level.SEVERE, null, ex);
         JOptionPane.showMessageDialog(null, "Nie można utworzyć strumienia wyjściowego!Sprawdź uprawnienia aplikacji.");
          }
          throw new UnsupportedOperationException("Not supported yet.");
            }
        
         
        
     };
        work.execute();
        
} 
     
  }
   public String GetSelectVersion(){
        return (String)module_version.getSelectedItem();
    //&&&&&&&&&&&&&&&&&&&&&&Koniec glowengo watku%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
  }
   class PortListener implements ActionListener{
    @Override
    public void actionPerformed(ActionEvent event) {
         serial_port = new SerialPort((String)ports.getSelectedItem());
 
    }
    }

//static class SerialPortReader implements SerialPortEventListener {
//        
//        @Override
//        public void serialEvent(SerialPortEvent event) {
//            if(event.isRXCHAR()){//If data is available
//                    try {
//                        tempdata = serial_port.readString();
//                    }
//                    catch (SerialPortException ex) {
//                        System.out.println(ex);
//                    }
//                
//            }
//            
//            }
//        }
//  
//
}