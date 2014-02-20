package Main;

import Features.Clocks;
import Features.DebugFile;
import Features.Initialize;
import Features.Macros;
import Features.SavePackage;
import Levels.GameLevel;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

public class MainScreen extends javax.swing.JFrame {

   public MainScreen() {
      initComponents();
      DebugFile.initialize(); //This should be the very first thing to start
      Clocks.masterClock.start();
      this.setExtendedState(MAXIMIZED_BOTH);
      this.addWindowListener(new WindowAdapter() {
         @Override
         public void windowClosing(WindowEvent e) {
            System.exit(0);
         }
      });

      keyConfig = new KeyConfigBoard();
      keyConfig.setVisible(false);
      difficulty = DEFAULT_DIFFICULTY;
   }

   /**
    * This method is called from within the constructor to initialize the form. WARNING: Do NOT
    * modify this code. The content of this method is always regenerated by the Form Editor.
    */
   @SuppressWarnings("unchecked")
   // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
   private void initComponents() {

      buttonGroup1 = new javax.swing.ButtonGroup();
      bStart = new javax.swing.JButton();
      bLoad = new javax.swing.JButton();
      loading = new javax.swing.JProgressBar();
      jLabel1 = new javax.swing.JLabel();
      jLabel2 = new javax.swing.JLabel();
      rbForKids = new javax.swing.JRadioButton();
      rbNormal = new javax.swing.JRadioButton();
      rbExpert = new javax.swing.JRadioButton();
      rbInsane = new javax.swing.JRadioButton();
      bOption = new javax.swing.JButton();
      rbNewbie = new javax.swing.JRadioButton();
      jLabel3 = new javax.swing.JLabel();
      rbBasicTutorial = new javax.swing.JRadioButton();
      rbAdvancedTutorial = new javax.swing.JRadioButton();
      jMenuBar1 = new javax.swing.JMenuBar();
      jMenu1 = new javax.swing.JMenu();
      miStartGame = new javax.swing.JMenuItem();
      miKeyConfig = new javax.swing.JMenuItem();
      miLoadGame = new javax.swing.JMenuItem();
      jSeparator1 = new javax.swing.JPopupMenu.Separator();
      miExit = new javax.swing.JMenuItem();

      setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

      bStart.setText("Start");
      bStart.addActionListener(new java.awt.event.ActionListener() {
         public void actionPerformed(java.awt.event.ActionEvent evt) {
            bStartActionPerformed(evt);
         }
      });

      bLoad.setText("Load");
      bLoad.addActionListener(new java.awt.event.ActionListener() {
         public void actionPerformed(java.awt.event.ActionEvent evt) {
            bLoadActionPerformed(evt);
         }
      });

      loading.setString("Waiting");
      loading.setStringPainted(true);

      jLabel1.setFont(new java.awt.Font("VNI-Algerian", 3, 24)); // NOI18N
      jLabel1.setForeground(new java.awt.Color(255, 0, 0));
      jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
      jLabel1.setText("The Puck Game");

      jLabel2.setFont(new java.awt.Font("Times New Roman", 1, 18)); // NOI18N
      jLabel2.setText("Difficulty");

      buttonGroup1.add(rbForKids);
      rbForKids.setText("For kids");
      rbForKids.addActionListener(new java.awt.event.ActionListener() {
         public void actionPerformed(java.awt.event.ActionEvent evt) {
            rbForKidsActionPerformed(evt);
         }
      });

      buttonGroup1.add(rbNormal);
      rbNormal.setSelected(true);
      rbNormal.setText("Normal");
      rbNormal.addActionListener(new java.awt.event.ActionListener() {
         public void actionPerformed(java.awt.event.ActionEvent evt) {
            rbNormalActionPerformed(evt);
         }
      });

      buttonGroup1.add(rbExpert);
      rbExpert.setText("Expert");
      rbExpert.addActionListener(new java.awt.event.ActionListener() {
         public void actionPerformed(java.awt.event.ActionEvent evt) {
            rbExpertActionPerformed(evt);
         }
      });

      buttonGroup1.add(rbInsane);
      rbInsane.setText("Insane");
      rbInsane.addActionListener(new java.awt.event.ActionListener() {
         public void actionPerformed(java.awt.event.ActionEvent evt) {
            rbInsaneActionPerformed(evt);
         }
      });

      bOption.setFont(new java.awt.Font("Times New Roman", 1, 14)); // NOI18N
      bOption.setText("Key Configuration");
      bOption.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
      bOption.addActionListener(new java.awt.event.ActionListener() {
         public void actionPerformed(java.awt.event.ActionEvent evt) {
            bOptionActionPerformed(evt);
         }
      });

      buttonGroup1.add(rbNewbie);
      rbNewbie.setText("Newbie");
      rbNewbie.addActionListener(new java.awt.event.ActionListener() {
         public void actionPerformed(java.awt.event.ActionEvent evt) {
            rbNewbieActionPerformed(evt);
         }
      });

      jLabel3.setFont(new java.awt.Font("Times New Roman", 1, 18)); // NOI18N
      jLabel3.setText("Tutorials");

      buttonGroup1.add(rbBasicTutorial);
      rbBasicTutorial.setText("Basic Tutorial");
      rbBasicTutorial.addActionListener(new java.awt.event.ActionListener() {
         public void actionPerformed(java.awt.event.ActionEvent evt) {
            rbBasicTutorialActionPerformed(evt);
         }
      });

      buttonGroup1.add(rbAdvancedTutorial);
      rbAdvancedTutorial.setText("Advance Tutorial");
      rbAdvancedTutorial.addActionListener(new java.awt.event.ActionListener() {
         public void actionPerformed(java.awt.event.ActionEvent evt) {
            rbAdvancedTutorialActionPerformed(evt);
         }
      });

      jMenu1.setText("Game");

      miStartGame.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_ENTER, java.awt.event.InputEvent.CTRL_MASK));
      miStartGame.setText("Start Game");
      miStartGame.addActionListener(new java.awt.event.ActionListener() {
         public void actionPerformed(java.awt.event.ActionEvent evt) {
            miStartGameActionPerformed(evt);
         }
      });
      jMenu1.add(miStartGame);

      miKeyConfig.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_O, java.awt.event.InputEvent.CTRL_MASK));
      miKeyConfig.setText("Key Configuration");
      miKeyConfig.addActionListener(new java.awt.event.ActionListener() {
         public void actionPerformed(java.awt.event.ActionEvent evt) {
            miKeyConfigActionPerformed(evt);
         }
      });
      jMenu1.add(miKeyConfig);

      miLoadGame.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_L, java.awt.event.InputEvent.CTRL_MASK));
      miLoadGame.setText("Load Game");
      jMenu1.add(miLoadGame);
      jMenu1.add(jSeparator1);

      miExit.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_X, java.awt.event.InputEvent.ALT_MASK));
      miExit.setText("Exit");
      miExit.addActionListener(new java.awt.event.ActionListener() {
         public void actionPerformed(java.awt.event.ActionEvent evt) {
            miExitActionPerformed(evt);
         }
      });
      jMenu1.add(miExit);

      jMenuBar1.add(jMenu1);

      setJMenuBar(jMenuBar1);

      javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
      getContentPane().setLayout(layout);
      layout.setHorizontalGroup(
         layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
         .addGroup(layout.createSequentialGroup()
            .addComponent(bStart, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
               .addGroup(layout.createSequentialGroup()
                  .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                  .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                     .addGroup(layout.createSequentialGroup()
                        .addComponent(loading, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED))
                     .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                           .addComponent(rbExpert)
                           .addComponent(rbInsane)
                           .addComponent(rbNormal)
                           .addComponent(jLabel2)
                           .addGroup(layout.createSequentialGroup()
                              .addComponent(rbForKids)
                              .addGap(18, 18, 18)
                              .addComponent(rbNewbie)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 215, Short.MAX_VALUE)
                        .addComponent(bOption, javax.swing.GroupLayout.PREFERRED_SIZE, 163, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(10, 10, 10))))
               .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                  .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                  .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 299, javax.swing.GroupLayout.PREFERRED_SIZE)
                  .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
               .addGroup(layout.createSequentialGroup()
                  .addGap(18, 18, 18)
                  .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                     .addComponent(rbBasicTutorial)
                     .addComponent(jLabel3)
                     .addComponent(rbAdvancedTutorial))
                  .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
            .addComponent(bLoad, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
      );
      layout.setVerticalGroup(
         layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
         .addComponent(bLoad, javax.swing.GroupLayout.Alignment.CENTER, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
         .addComponent(bStart, javax.swing.GroupLayout.Alignment.CENTER, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
         .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
               .addGroup(layout.createSequentialGroup()
                  .addGap(36, 36, 36)
                  .addComponent(jLabel2)
                  .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                  .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                     .addComponent(rbForKids)
                     .addComponent(rbNewbie))
                  .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                  .addComponent(rbNormal)
                  .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                  .addComponent(rbExpert)
                  .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                  .addComponent(rbInsane))
               .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                  .addGap(53, 53, 53)
                  .addComponent(bOption, javax.swing.GroupLayout.PREFERRED_SIZE, 104, javax.swing.GroupLayout.PREFERRED_SIZE)))
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 24, Short.MAX_VALUE)
            .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 86, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 26, Short.MAX_VALUE)
            .addComponent(loading, javax.swing.GroupLayout.PREFERRED_SIZE, 99, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addGap(18, 18, 18)
            .addComponent(jLabel3)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(rbBasicTutorial)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(rbAdvancedTutorial)
            .addContainerGap(228, Short.MAX_VALUE))
      );

      bOption.getAccessibleContext().setAccessibleDescription("");

      pack();
   }// </editor-fold>//GEN-END:initComponents

    private void bStartActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bStartActionPerformed
       startGame(true);
    }//GEN-LAST:event_bStartActionPerformed

    private void miExitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_miExitActionPerformed
       System.exit(0);
    }//GEN-LAST:event_miExitActionPerformed

    private void miStartGameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_miStartGameActionPerformed
       startGame(true);
    }//GEN-LAST:event_miStartGameActionPerformed

    private void miKeyConfigActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_miKeyConfigActionPerformed
       keyConfig.setVisible(true);
    }//GEN-LAST:event_miKeyConfigActionPerformed

    private void rbExpertActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rbExpertActionPerformed
       difficulty = GameLevel.EXPERT;
    }//GEN-LAST:event_rbExpertActionPerformed

    private void bOptionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bOptionActionPerformed
       keyConfig.setVisible(true);
    }//GEN-LAST:event_bOptionActionPerformed

    private void rbForKidsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rbForKidsActionPerformed
       difficulty = GameLevel.FOR_KIDS;
    }//GEN-LAST:event_rbForKidsActionPerformed

    private void rbNormalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rbNormalActionPerformed
       difficulty = GameLevel.NORMAL;
    }//GEN-LAST:event_rbNormalActionPerformed

    private void rbInsaneActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rbInsaneActionPerformed
       difficulty = GameLevel.BETTER_HAVE_GOOD_PROCESSOR;
       JOptionPane.showMessageDialog(null, "Make sure you don't burn your processor by playing in this mode!", "Warning", JOptionPane.WARNING_MESSAGE);
    }//GEN-LAST:event_rbInsaneActionPerformed

    private void rbNewbieActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rbNewbieActionPerformed
       difficulty = GameLevel.NEWBIE;
    }//GEN-LAST:event_rbNewbieActionPerformed

    private void bLoadActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bLoadActionPerformed
       do {
          JFileChooser chooser = new JFileChooser("D:\\Study\\Java\\Projects\\ThePuckGame");
          chooser.setApproveButtonText("Load Game");
          int chosen = chooser.showOpenDialog(MainScreen.this);
          if (chosen == JFileChooser.APPROVE_OPTION) {
             difficulty = SavePackage.loadSavePackage(chooser.getSelectedFile());
             break;
          } else if (chosen == JFileChooser.CANCEL_OPTION) {
             return;
          }
       } while (true);
       startGame(false);
    }//GEN-LAST:event_bLoadActionPerformed

   private void rbBasicTutorialActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rbBasicTutorialActionPerformed
      difficulty = GameLevel.BASIC_TUTORIAL;
   }//GEN-LAST:event_rbBasicTutorialActionPerformed

   private void rbAdvancedTutorialActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rbAdvancedTutorialActionPerformed
      difficulty = GameLevel.ADVANCED_TUTORIAL;
   }//GEN-LAST:event_rbAdvancedTutorialActionPerformed

   public static void main(String args[]) {
      /* Set the Nimbus look and feel */
      //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
       * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html
       */
      try {
         for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
            if ("Nimbus".equals(info.getName())) {
               javax.swing.UIManager.setLookAndFeel(info.getClassName());
               break;
            }
         }
      } catch (ClassNotFoundException ex) {
         java.util.logging.Logger.getLogger(MainScreen.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
      } catch (InstantiationException ex) {
         java.util.logging.Logger.getLogger(MainScreen.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
      } catch (IllegalAccessException ex) {
         java.util.logging.Logger.getLogger(MainScreen.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
      } catch (javax.swing.UnsupportedLookAndFeelException ex) {
         java.util.logging.Logger.getLogger(MainScreen.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
      }
      //</editor-fold>

      /* Create and display the form */
      java.awt.EventQueue.invokeLater(new Runnable() {
         public void run() {
            new MainScreen().setVisible(true);
         }
      });
   }
   // Variables declaration - do not modify//GEN-BEGIN:variables
   private javax.swing.JButton bLoad;
   private javax.swing.JButton bOption;
   private javax.swing.JButton bStart;
   private javax.swing.ButtonGroup buttonGroup1;
   private javax.swing.JLabel jLabel1;
   private javax.swing.JLabel jLabel2;
   private javax.swing.JLabel jLabel3;
   private javax.swing.JMenu jMenu1;
   private javax.swing.JMenuBar jMenuBar1;
   private javax.swing.JPopupMenu.Separator jSeparator1;
   private javax.swing.JProgressBar loading;
   private javax.swing.JMenuItem miExit;
   private javax.swing.JMenuItem miKeyConfig;
   private javax.swing.JMenuItem miLoadGame;
   private javax.swing.JMenuItem miStartGame;
   private javax.swing.JRadioButton rbAdvancedTutorial;
   private javax.swing.JRadioButton rbBasicTutorial;
   private javax.swing.JRadioButton rbExpert;
   private javax.swing.JRadioButton rbForKids;
   private javax.swing.JRadioButton rbInsane;
   private javax.swing.JRadioButton rbNewbie;
   private javax.swing.JRadioButton rbNormal;
   // End of variables declaration//GEN-END:variables
   private KeyConfigBoard keyConfig;
   private byte difficulty;
   private Game game;
   private Initialize init;
   public static final byte DEFAULT_DIFFICULTY = GameLevel.NORMAL;

   private void startGame(boolean isNewGame) {
      if (isNewGame) {
         GameLevel.setDifficulty(difficulty);
      }

      keyConfig.setVisible(false);
      init = new Initialize(loading, this);
      init.start();
   }

   public void endGame() {
      game.setVisible(false);
      SavePackage.restoreDefault();
      loading.setString("Initialization completed");
      loading.setValue(0);
      this.setVisible(true);
   }

   public byte difficulty() {
      return difficulty;
   }

   public Macros macros() {
      return keyConfig.macros();
   }

   public void setGame(Game game) {
      this.game = game;
   }

   public Game game() {
      return game;
   }
}