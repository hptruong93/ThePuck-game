package Main;

import Main.Units.Living.LivingUnit;
import Main.Units.Living.MainBuilding;
import Main.Units.Living.Puck.FirstSkill;
import Main.Units.Living.Puck.Puck;
import Main.Units.Living.Puck.SkillCoolDown;
import Main.Units.NonLiving.UniversalEffect;
import Features.Audio;
import Features.Clocks;
import Features.DebugFile;
import Utilities.FileUtilities;
import Utilities.Geometry;
import Features.Macros;
import Main.Units.Living.Boss.Archon.Archon;
import Main.Units.NonLiving.Marker;
import Main.Units.NonLiving.MessageText;
import Utilities.Pointt;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.GroupLayout.ParallelGroup;
import javax.swing.GroupLayout.SequentialGroup;

public class Game extends JFrame {

   private MainScreen welcomeScreen;
   private MainBuilding mainBuilding;
   private Puck puck;
   private HashSet<Marker> markers;
   private HashSet<LivingUnit> enemies;
   private HashSet<LivingUnit> selectedUnits;
   protected HashSet<UniversalEffect> visualEffects;
   private Board board;
   private MiniMap miniMap;
   private boolean paused;
   private int processorID, graphicsID;
   private Pointt focus;
   private MessageText infoMessage;
   private SkillCoolDown bFirstSkill;
   private SkillCoolDown bSecondSkill;
   private SkillCoolDown bThirdSkill;
   private SkillCoolDown bUltimate;
   private JRadioButton[] rbFirstSkill;
   private JRadioButton[] rbSecondSkill;
   private JRadioButton[] rbThirdSkill;
   private JRadioButton[] rbUltimate;
   private JRadioButton[] rbBonus;
   private JLabel lHealth, lName, lStage, lKill;
   public static ProcessingUnit map = new ProcessingUnit();
   private static boolean soundOn;
   private static boolean backGroundMusicOn;
   private Cursor defaultCursor, targetCursor;
   private Image backgroundMain, backgroundBot;

   public Game(MainScreen welcomeScreen) {
      this.welcomeScreen = welcomeScreen;
      setupFrame();
      setupInputs(welcomeScreen.macros());
      initialize();
   }

   private void setupFrame() {
      setSize(ProcessingUnit.SCREEN());
      setResizable(false);
      setUndecorated(true);
      setFocusTraversalKeysEnabled(false);
      addWindowListener(new WindowAdapter() {
         @Override
         public void windowClosing(WindowEvent e) {
            if (ProcessingUnit.TESTING) {
               System.exit(DebugFile.EXIT_SUCCESS);
            } else {
               map.gameOver(Game.this);
            }
         }
      });

      Box top = Box.createHorizontalBox();
      top.setBorder(BorderFactory.createLineBorder(Color.BLUE, map.BORDER_WIDTH(), true));
      top.setPreferredSize(new Dimension(ProcessingUnit.SCREEN().width, (int) (ProcessingUnit.SCREEN().height - map.MINI_SIZEY())));
      board = new Board();
      board.setFocusTraversalKeysEnabled(false);
      top.add(board);

      Box mini = Box.createVerticalBox();
      miniMap = new MiniMap();
      miniMap.setFocusTraversalKeysEnabled(false);
      mini.add(miniMap);

      bFirstSkill = new SkillCoolDown("FirstSkill.png", Color.BLUE, map);
      bSecondSkill = new SkillCoolDown("SecondSkill.jpg", Color.BLACK, map);
      bThirdSkill = new SkillCoolDown("ThirdSkill.jpg", Color.WHITE, map);
      bUltimate = new SkillCoolDown("Ultimate.jpg", Color.BLUE, map);

      bFirstSkill.setFocusable(false);
      bSecondSkill.setFocusable(false);
      bThirdSkill.setFocusable(false);
      bUltimate.setFocusable(false);
      bFirstSkill.setFocusTraversalKeysEnabled(false);
      bSecondSkill.setFocusTraversalKeysEnabled(false);
      bThirdSkill.setFocusTraversalKeysEnabled(false);
      bUltimate.setFocusTraversalKeysEnabled(false);


      rbFirstSkill = new JRadioButton[1];
      rbFirstSkill[0] = new JRadioButton("Damage");
      rbFirstSkill[0].setFocusable(false);
      rbFirstSkill[0].setSelected(true);
      rbFirstSkill[0].setEnabled(false);
      rbFirstSkill[0].setFocusTraversalKeysEnabled(false);
      rbFirstSkill[0].setOpaque(false);

      rbSecondSkill = new JRadioButton[3];
      rbSecondSkill[0] = new JRadioButton("Slower");
      rbSecondSkill[1] = new JRadioButton("Faster");
      rbSecondSkill[2] = new JRadioButton("Damage");
      rbSecondSkill[2].setSelected(true);
      ButtonGroup secondGroup = new ButtonGroup();
      for (int i = 0; i < rbSecondSkill.length; i++) {
         rbSecondSkill[i].setFocusable(false);
         secondGroup.add(rbSecondSkill[i]);
         rbSecondSkill[i].setFocusTraversalKeysEnabled(false);
         rbSecondSkill[i].setOpaque(false);
      }


      rbThirdSkill = new JRadioButton[1];
      rbThirdSkill[0] = new JRadioButton("Disabled");
      rbThirdSkill[0].setSelected(true);
      rbThirdSkill[0].setFocusable(false);
      rbThirdSkill[0].setEnabled(false);
      rbThirdSkill[0].setFocusTraversalKeysEnabled(false);
      rbThirdSkill[0].setOpaque(false);

      rbUltimate = new JRadioButton[3];
      rbUltimate[0] = new JRadioButton("Radius");
      rbUltimate[1] = new JRadioButton("Speed");
      rbUltimate[2] = new JRadioButton("Life Steal");
      rbUltimate[0].setSelected(true);
      ButtonGroup ultimateGroup = new ButtonGroup();
      for (int i = 0; i < rbUltimate.length; i++) {
         rbUltimate[i].setFocusable(false);
         rbUltimate[i].setFocusTraversalKeysEnabled(false);
         ultimateGroup.add(rbUltimate[i]);
         rbUltimate[i].setOpaque(false);
      }


      rbBonus = new JRadioButton[2];
      rbBonus[0] = new JRadioButton("Skill Bonus");
      rbBonus[1] = new JRadioButton("Health Bonus");
      rbBonus[0].setSelected(true);
      ButtonGroup bonusGroup = new ButtonGroup();
      for (int i = 0; i < rbBonus.length; i++) {
         rbBonus[i].setFocusable(false);
         rbBonus[i].setFocusTraversalKeysEnabled(false);
         bonusGroup.add(rbBonus[i]);
      }


      Font infoFont = new Font("Monotype Corsiva", Font.BOLD, 45);
      lHealth = new JLabel("011010/0101010");
      lHealth.setFont(infoFont);
      lHealth.setFocusable(false);
      lHealth.setFocusTraversalKeysEnabled(false);
      lHealth.setOpaque(false);

      lName = new JLabel("");
      lName.setFont(infoFont);
      lName.setFocusable(false);
      lName.setFocusTraversalKeysEnabled(false);
      lHealth.setOpaque(false);

      lStage = new JLabel("Stage: 1");
      lStage.setFont(infoFont);
      lStage.setFocusable(false);
      lStage.setFocusTraversalKeysEnabled(false);
      lStage.setOpaque(false);

      lKill = new JLabel("Kills: 0000000");
      lKill.setFont(infoFont);
      lKill.setFocusable(false);
      lKill.setFocusTraversalKeysEnabled(false);
      lKill.setOpaque(false);

      setupRadioButtonListener();
      GroupLayout lo = new GroupLayout(getContentPane());
      getContentPane().setLayout(lo);

      board.setPreferredSize(new Dimension(ProcessingUnit.SCREEN().width, (int) (ProcessingUnit.SCREEN().height - map.MINI_SIZEY())));
      //Horizontal
      {
         ParallelGroup total = lo.createParallelGroup(GroupLayout.Alignment.LEADING);
         total.addComponent(top);
         SequentialGroup bot = lo.createSequentialGroup();
         {
            mini.setPreferredSize(new Dimension((int) map.MINI_SIZEX(), (int) (map.MINI_SIZEY())));
            bot.addComponent(mini, (int) map.MINI_SIZEX(), (int) map.MINI_SIZEX(), (int) map.MINI_SIZEX());
            bot.addGap((int) (map.MINI_SIZEY() / 4));
            ParallelGroup buttonArea = lo.createParallelGroup(GroupLayout.Alignment.LEADING);
            {
               buttonArea.addGap(2 * map.BORDER_WIDTH());
               SequentialGroup buttons = lo.createSequentialGroup();
               {
                  int buttonSize = (int) (map.MINI_SIZEY() / 3);

                  ParallelGroup firstSkill = lo.createParallelGroup(GroupLayout.Alignment.LEADING);
                  firstSkill.addComponent(bFirstSkill, buttonSize, buttonSize, buttonSize);
                  firstSkill.addComponent(rbFirstSkill[0]);
                  buttons.addGroup(firstSkill);

                  ParallelGroup secondSkill = lo.createParallelGroup(GroupLayout.Alignment.LEADING);
                  secondSkill.addComponent(bSecondSkill, buttonSize, buttonSize, buttonSize);
                  secondSkill.addComponent(rbSecondSkill[0]);
                  secondSkill.addComponent(rbSecondSkill[1]);
                  secondSkill.addComponent(rbSecondSkill[2]);
                  buttons.addGroup(secondSkill);

                  ParallelGroup thirdSkill = lo.createParallelGroup(GroupLayout.Alignment.LEADING);
                  thirdSkill.addComponent(bThirdSkill, buttonSize, buttonSize, buttonSize);
                  thirdSkill.addComponent(rbThirdSkill[0]);
                  buttons.addGroup(thirdSkill);

                  ParallelGroup ult = lo.createParallelGroup(GroupLayout.Alignment.LEADING);
                  ult.addComponent(bUltimate, buttonSize, buttonSize, buttonSize);

                  for (int i = 0; i < rbUltimate.length; i++) {
                     ult.addComponent(rbUltimate[i]);
                  }

                  buttons.addGroup(ult);
               }
               buttonArea.addGroup(buttons);
               buttonArea.addGap((int) (map.MINI_SIZEY() / 3));
            }
            bot.addGroup(buttonArea);

            ParallelGroup bonusArea = lo.createParallelGroup(GroupLayout.Alignment.LEADING);
            {
               bonusArea.addGap((int) map.MINI_SIZEY() / 2);
               bonusArea.addComponent(rbBonus[0]);
               bonusArea.addComponent(rbBonus[1]);
               bonusArea.addComponent(lKill);
            }
            bot.addGroup(bonusArea);

            ParallelGroup selectedUnitInfo = lo.createParallelGroup(GroupLayout.Alignment.LEADING);
            {
               selectedUnitInfo.addGap((int) map.MINI_SIZEY() / 2);
               selectedUnitInfo.addComponent(lName);
               selectedUnitInfo.addComponent(lHealth);
               selectedUnitInfo.addComponent(lStage);
            }
            bot.addGroup(selectedUnitInfo);

            bot.addContainerGap();
         }
         total.addGroup(bot);
         lo.setHorizontalGroup(total);
      }

      //Vertical
      {
         SequentialGroup total = lo.createSequentialGroup();
         total.addComponent(top);
         ParallelGroup bot = lo.createParallelGroup(GroupLayout.Alignment.LEADING);
         {
            bot.addComponent(mini, (int) map.MINI_SIZEY(), (int) map.MINI_SIZEY(), (int) map.MINI_SIZEY());
            SequentialGroup buttonArea = lo.createSequentialGroup();
            {
               buttonArea.addGap(2 * map.BORDER_WIDTH());
               ParallelGroup buttons = lo.createParallelGroup(GroupLayout.Alignment.LEADING);
               {
                  buttons.addGap((int) map.MINI_SIZEY() / 3);
                  int buttonSize = (int) map.MINI_SIZEY() / 3;

                  SequentialGroup firstSkill = lo.createSequentialGroup();
                  firstSkill.addComponent(bFirstSkill, buttonSize, buttonSize, buttonSize);
                  firstSkill.addComponent(rbFirstSkill[0]);
                  buttons.addGroup(firstSkill);

                  SequentialGroup secondSkill = lo.createSequentialGroup();
                  secondSkill.addComponent(bSecondSkill, buttonSize, buttonSize, buttonSize);
                  secondSkill.addComponent(rbSecondSkill[0]);
                  secondSkill.addComponent(rbSecondSkill[1]);
                  secondSkill.addComponent(rbSecondSkill[2]);
                  buttons.addGroup(secondSkill);

                  SequentialGroup thirdSkill = lo.createSequentialGroup();
                  thirdSkill.addComponent(bThirdSkill, buttonSize, buttonSize, buttonSize);
                  thirdSkill.addComponent(rbThirdSkill[0]);
                  buttons.addGroup(thirdSkill);

                  SequentialGroup ult = lo.createSequentialGroup();
                  ult.addComponent(bUltimate, buttonSize, buttonSize, buttonSize);

                  for (int i = 0; i < rbUltimate.length; i++) {
                     ult.addComponent(rbUltimate[i]);
                  }

                  buttons.addGroup(ult);

                  SequentialGroup bonus = lo.createSequentialGroup();
                  bonus.addComponent(rbBonus[0]);
                  bonus.addComponent(rbBonus[1]);
                  bonus.addComponent(lKill);
                  buttons.addGroup(bonus);

                  SequentialGroup healthBar = lo.createSequentialGroup();
                  healthBar.addComponent(lName);
                  healthBar.addComponent(lHealth);
                  healthBar.addComponent(lStage);
                  buttons.addGroup(healthBar);
               }

               buttonArea.addGroup(buttons);
               buttonArea.addContainerGap();
            }
            bot.addGroup(buttonArea);
         }
         total.addGroup(bot);
         lo.setVerticalGroup(total);
      }
   }

   private void setupRadioButtonListener() {
      for (int i = 0; i < rbSecondSkill.length; i++) {
         final int j = i;
         rbSecondSkill[i].addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
               if (rbSecondSkill[j].isSelected()) {
                  puck.secondSkill().setBonusType(j);
               }
            }
         });
      }
      for (int i = 0; i < rbUltimate.length; i++) {
         final int j = i;
         rbUltimate[i].addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
               if (rbUltimate[j].isSelected()) {
                  puck.ultimate().setBonusType(j);
               }
            }
         });
      }
      for (int i = 0; i < rbBonus.length; i++) {
         final int j = i;
         rbBonus[i].addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
               if (rbBonus[j].isSelected()) {
                  puck.setBonusType(j);
                  boolean current = puck.bonusType() == Puck.SKILL_BONUS;
                  for (int i = 0; i < rbSecondSkill.length; i++) {
                     rbSecondSkill[i].setEnabled(current);
                  }
                  for (int i = 0; i < rbUltimate.length; i++) {
                     rbUltimate[i].setEnabled(current);
                  }
               }
            }
         });
      }
   }

   private class Keyboard extends AbstractAction {

      int button;

      Keyboard(int button) {
         this.button = button;
      }

      @Override
      public void actionPerformed(ActionEvent e) {
         puck.clearHold();
         Game.this.setCursor(defaultCursor);

         if (button == Macros.MUSIC_TOGGLE_INDEX) {
            backGroundMusicOn = !backGroundMusicOn;
         } else if (button == Macros.SAVE_INDEX) {
            FileUtilities.writeToFile(map.saveContent, "");
         } else if (button == Macros.PAUSE_INDEX) {
            if (!paused) {
               Clocks.masterClock.stop();
            } else {
               Clocks.masterClock.start();
            }
            paused = !paused;
         } else if (!paused) {
            if (button == Macros.BLINK_INDEX) { //Blink
               if (puck.setHoldBlink(true)) {
                  Game.this.setCursor(targetCursor);
               }
            } else if (button == Macros.ATTACK_INDEX) { //Attack
               puck.setHoldAttack(true);
               Game.this.setCursor(targetCursor);
            } else if (button == Macros.STOP_INDEX) {//Stop
               puck.setDestination(puck.position());
            } else if (button == Macros.FIRST_SKILL_INDEX) {//First Skill
               if (puck.setHoldFirstSkill(true)) {
                  Game.this.setCursor(targetCursor);
               }
            } else if (button == Macros.SECOND_SKILL_INDEX) {// Second Skill
               puck.secondSkill().setActivate(true);
            } else if (button == Macros.THIRD_SKILL_INDEX) {//Third Skill
               puck.setThirdSkill();
            } else if (button == Macros.ULTIMATE_SKILL_INDEX) {//Ultimate
               if (puck.setHoldUltimate(true)) {
                  Game.this.setCursor(targetCursor);
               }
            } else if (button == KeyEvent.VK_ESCAPE) { //Clear commands
               puck.clearHold();
            } else if (button == Macros.FOCUS_INDEX) { //Get focus
               Game.this.setCursor(defaultCursor);
               if (!mainBuilding.dead()) {
                  clearSelected();
                  puck.setSelected(true, selectedUnits);
                  focus = puck.miniPosition();
               }
            } else if (button == Macros.SOUND_TOGGLE_INDEX) {
               soundOn = !soundOn;
            } else if (button == Macros.SKILL_TOGGLE_INDEX) {//Toggle Skill - Health Bonus
               puck.setBonusType((puck.bonusType() + 1) % 2);
               rbBonus[puck.bonusType()].setSelected(true);
               boolean current = puck.bonusType() == Puck.SKILL_BONUS;
               for (int i = 0; i < rbSecondSkill.length; i++) {
                  rbSecondSkill[i].setEnabled(current);
               }
               for (int i = 0; i < rbUltimate.length; i++) {
                  rbUltimate[i].setEnabled(current);
               }
               Audio.playSound(Audio.TOGGLE);
            } else if (button == Macros.FIRST_SKILL_BONUS_TOGGLE_INDEX) {
            } else if (button == Macros.SECOND_SKILL_BONUS_TOGGLE_INDEX) {//Toggle SecondSkill bonus
               if (puck.bonusType() == Puck.SKILL_BONUS) {
                  puck.secondSkill().setBonusType((puck.secondSkill().bonusType() + 1) % rbSecondSkill.length);
                  rbSecondSkill[puck.secondSkill().bonusType()].setSelected(true);
                  Audio.playSound(Audio.TOGGLE2);
               }
            } else if (button == Macros.THIRD_SKILL_BONUS_TOGGLE_INDEX) {
            } else if (button == Macros.ULTIMATE_SKILL_BONUS_TOGGLE_INDEX) {//Toggle Ultimate bonus
               if (puck.bonusType() == Puck.SKILL_BONUS) {
                  puck.ultimate().setBonusType((puck.ultimate().bonusType() + 1) % rbUltimate.length);
                  rbUltimate[puck.ultimate().bonusType()].setSelected(true);
                  Audio.playSound(Audio.TOGGLE2);
               }
            }
         }
      }
   }

   private void setupInputs(Macros macros) {
      board.getInputMap().clear();
      board.getActionMap().clear();

      board.getInputMap().put(KeyStroke.getKeyStroke(macros.hotKeys()[Macros.BLINK_INDEX], 0), "Blink");
      board.getActionMap().put("Blink", new Keyboard(Macros.BLINK_INDEX));

      board.getInputMap().put(KeyStroke.getKeyStroke(macros.hotKeys()[Macros.FIRST_SKILL_INDEX], 0), "First Skill");
      board.getActionMap().put("First Skill", new Keyboard(Macros.FIRST_SKILL_INDEX));

      board.getInputMap().put(KeyStroke.getKeyStroke(macros.hotKeys()[Macros.SECOND_SKILL_INDEX], 0), "Second Skill");
      board.getActionMap().put("Second Skill", new Keyboard(Macros.SECOND_SKILL_INDEX));

      board.getInputMap().put(KeyStroke.getKeyStroke(macros.hotKeys()[Macros.THIRD_SKILL_INDEX], 0), "Third Skill");
      board.getActionMap().put("Third Skill", new Keyboard(Macros.THIRD_SKILL_INDEX));

      board.getInputMap().put(KeyStroke.getKeyStroke(macros.hotKeys()[Macros.ULTIMATE_SKILL_INDEX], 0), "Ultimate");
      board.getActionMap().put("Ultimate", new Keyboard(Macros.ULTIMATE_SKILL_INDEX));

      board.getInputMap().put(KeyStroke.getKeyStroke(macros.hotKeys()[Macros.ATTACK_INDEX], 0), "Attack");
      board.getActionMap().put("Attack", new Keyboard(Macros.ATTACK_INDEX));

      board.getInputMap().put(KeyStroke.getKeyStroke(macros.hotKeys()[Macros.STOP_INDEX], 0), "Stop");
      board.getActionMap().put("Stop", new Keyboard(Macros.STOP_INDEX));

      board.getInputMap().put(KeyStroke.getKeyStroke(macros.hotKeys()[Macros.FOCUS_INDEX], 0), "Focus");
      board.getActionMap().put("Focus", new Keyboard(Macros.FOCUS_INDEX));

      board.getInputMap().put(KeyStroke.getKeyStroke(macros.hotKeys()[Macros.SKILL_TOGGLE_INDEX], 0), "SwitchBonus");
      board.getActionMap().put("SwitchBonus", new Keyboard(Macros.SKILL_TOGGLE_INDEX));

      board.getInputMap().put(KeyStroke.getKeyStroke(macros.hotKeys()[Macros.FIRST_SKILL_BONUS_TOGGLE_INDEX], 0), "FirstSkillSwitch");
      board.getActionMap().put("FirstSkillSwitch", new Keyboard(Macros.FIRST_SKILL_BONUS_TOGGLE_INDEX));

      board.getInputMap().put(KeyStroke.getKeyStroke(macros.hotKeys()[Macros.SECOND_SKILL_BONUS_TOGGLE_INDEX], 0), "SecondSkillSwitch");
      board.getActionMap().put("SecondSkillSwitch", new Keyboard(Macros.SECOND_SKILL_BONUS_TOGGLE_INDEX));

      board.getInputMap().put(KeyStroke.getKeyStroke(macros.hotKeys()[Macros.THIRD_SKILL_BONUS_TOGGLE_INDEX], 0), "ThirdSkillSwitch");
      board.getActionMap().put("ThirdSkillSwitch", new Keyboard(Macros.THIRD_SKILL_BONUS_TOGGLE_INDEX));

      board.getInputMap().put(KeyStroke.getKeyStroke(macros.hotKeys()[Macros.ULTIMATE_SKILL_BONUS_TOGGLE_INDEX], 0), "UltimateSwitch");
      board.getActionMap().put("UltimateSwitch", new Keyboard(Macros.ULTIMATE_SKILL_BONUS_TOGGLE_INDEX));

      board.getInputMap().put(KeyStroke.getKeyStroke(macros.hotKeys()[Macros.SOUND_TOGGLE_INDEX], 0), "SoundToggle");
      board.getActionMap().put("SoundToggle", new Keyboard(Macros.SOUND_TOGGLE_INDEX));

      board.getInputMap().put(KeyStroke.getKeyStroke(macros.hotKeys()[Macros.MUSIC_TOGGLE_INDEX], 0), "BackGroundMusic");
      board.getActionMap().put("BackGroundMusic", new Keyboard(Macros.MUSIC_TOGGLE_INDEX));

      board.getInputMap().put(KeyStroke.getKeyStroke(macros.hotKeys()[Macros.SAVE_INDEX], 0), "SaveGame");
      board.getActionMap().put("SaveGame", new Keyboard(Macros.SAVE_INDEX));

      board.getInputMap().put(KeyStroke.getKeyStroke(macros.hotKeys()[Macros.PAUSE_INDEX], 0), "PauseGame");
      board.getActionMap().put("PauseGame", new Keyboard(Macros.PAUSE_INDEX));
   }

   private void initialize() {
      Toolkit tk = Toolkit.getDefaultToolkit();
      try {
         backgroundMain = ImageIO.read(new File("Background.jpg"));
         backgroundBot = ImageIO.read(new File("BackgroundBot.jpg"));

         Image tam = ImageIO.read(new File("Pointer.png"));
         defaultCursor = tk.createCustomCursor(tam, new Point(0, 0), "Pointer");
         tam = ImageIO.read(new File("Target.png"));
         targetCursor = tk.createCustomCursor(tam, new Point(10, 18), "Target");
         this.setCursor(defaultCursor);
      } catch (IOException ex) {
         Logger.getLogger(Game.class.getName()).log(Level.SEVERE, null, ex);
         DebugFile.printLog(ex);
         System.exit(0);
      }

      infoMessage = new MessageText("", MessageText.MESSAGE_FONT, Color.RED, MessageText.DEFAULT_EXIST_TIME, MessageText.DEFAULT_APPEAR_TIME, new Pointt(0, 0), 0);
      markers = new HashSet<>();
      visualEffects = new HashSet<>();
      enemies = new HashSet<>();
      selectedUnits = new HashSet<>();
   }

   @Override
   public void paint(Graphics g) {
      super.paint(g);
      Graphics2D a = (Graphics2D) g;
      a.setTransform(new AffineTransform());
      a.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.4f));
      a.drawImage(backgroundBot, (int) map.MINI_SIZEX(), (int) (ProcessingUnit.SCREEN().height - map.MINI_SIZEY()), null);
   }

   protected class Board extends JComponent {

      public Board() {
         addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
               Pointt clicked = new Pointt(e.getPoint());
               clicked = clicked.displayToReal(focus);
               LivingUnit targeted = puck.targetedCreep(clicked, enemies);

               if (e.getModifiers() == InputEvent.BUTTON3_MASK) {// Right click
                  puck.setAttackCommand(false);
                  if (!puck.holdBlink() && !puck.holdAttack() && !puck.holdFirstSkill()) {
                     if (targeted == null) {// No target. Move to that point
                        if (puck.position().distance(clicked) > puck.radius()) {
                           puck.updateMovement(clicked.clone());
                           puck.setTarget(null);
                        }
                     } else {// Target the current
                        puck.setTarget(targeted, puck.position());
                        targeted.setSelected(true, selectedUnits);
                     }
                  }
                  puck.clearHold();
                  Game.this.setCursor(defaultCursor);
               } else if (e.getModifiers() == InputEvent.BUTTON1_MASK) {// Left click
                  puck.setAttackCommand(false);
                  if (puck.holdBlink()) {
                     puck.blink(clicked.clone());
                  } else if (puck.holdAttack()) {
                     if (targeted == null) {// Update movement
                        puck.setAttackCommand(true); //Activate attack mode
                        puck.updateMovement(clicked.clone());
                     } else {//Target the current and update the destination
                        puck.setTarget(targeted, puck.position());
                     }
                  } else if (puck.holdFirstSkill()) {
                     double angle = Geometry.arcTan(clicked.getY() - puck.position().getY(), clicked.getX() - puck.position().getX(), clicked.getX() - puck.position().getX());
                     puck.setFirstSkill(new FirstSkill(Game.this, angle));
                     puck.setTarget(null);
                  } else if (puck.holdUltimate()) {
                     puck.ultimate().setActivate(true, clicked.clone());
                  } else {
                     clearSelected();
                     if (targeted != null) {
                        targeted.setSelected(true, selectedUnits);
                     } else {
                        puck.setSelected(true, selectedUnits);
                     }
                  }
                  puck.clearHold();
                  Game.this.setCursor(defaultCursor);
               }
            }
         });
      }

      @Override
      public void paint(Graphics g) {
         Graphics2D a = (Graphics2D) g;
         a.setColor(map.MAP_COLOR());
         a.fillRect(0, 0, getWidth(), getHeight());
         AffineTransform transform = AffineTransform.getTranslateInstance(map.BORDER_WIDTH(), map.BORDER_WIDTH());
         a.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

         //Plot main building
         mainBuilding.plot(a, transform, focus);

         if ((ProcessingUnit.lockFocus() == ProcessingUnit.WIN_LOCK()) || (ProcessingUnit.lockFocus() == ProcessingUnit.GAME_OVER_LOCK())) {
            return;
         }

         //Plot message
         infoMessage.moveNoCollision(map.PROCESSING_RATE());
         infoMessage.plot(a, transform);

         //Plot markers
         synchronized (markers) {
            for (Marker current : markers) {
               current.plot(a, transform, focus);
            }
         }

         //Plot visual effects
         synchronized (visualEffects) {
            for (Iterator<UniversalEffect> it = visualEffects.iterator(); it.hasNext();) {
               UniversalEffect current = it.next();
               current.plot(a, transform, focus);
            }
         }

         //Plot puck
         puck.plot(a, transform, focus);

         //Plot enemies
         transform = new AffineTransform();
         synchronized (enemies) {
            for (LivingUnit current : enemies) {
               current.plot(a, transform, focus);
            }
         }

         //Plot static skills
         Archon.plotEncircle(a, transform, focus);
      }
   }

   protected class MiniMap extends JComponent {

      private static final int REP_SIZE = 4;

      public MiniMap() {

         focus = new Pointt(20, 30);
         addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
               focus = new Pointt(e.getPoint()).focus();
            }
         });

         addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
               focus = new Pointt(e.getPoint()).focus();
               repaint();
            }
         });
      }

      @Override
      public void paint(Graphics g) {
         Graphics2D a = (Graphics2D) g;
         a.setColor(map.MAP_COLOR());
         a.fillRect(0, 0, getWidth(), getHeight());
         a.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

         //Adjust selected unit info
         if (!selectedUnits.isEmpty()) {
            synchronized (selectedUnits) {
               LivingUnit selected = selectedUnits.iterator().next();
               if (selected.dead()) {
                  lHealth.setText("");
                  lName.setText("");
               } else {
                  if (ProcessingUnit.TESTING) {
                     lName.setText(selected.health() + "");
                  } else {
                     lName.setText(selected.name());
                  }
                  lHealth.setText(Math.max((int) selected.health(), 0) + " / " + (int) selected.maxHealth());
               }
            }
         } else {
            if (!puck.dead()) {
               lName.setText(puck.name());
               lHealth.setText(Math.max((int) puck.health(), 0) + " / " + (int) puck.maxHealth());
            } else {
               lHealth.setText("");
               lName.setText("");
            }
         }

         //Kill and stage info
         lStage.setText("Stage: " + ProcessingUnit.stage());
         lKill.setText("Kill: " + puck.numberOfKills());

         //Plot viewing constraint
         a.setPaint(Color.BLACK);
         a.drawLine((int) map.MINI_SIZEX() - 1, 0, (int) map.MINI_SIZEX(), (int) (1000));
         a.draw(new Rectangle2D.Double(focus.getX() - map.FOCUS_WIDTH() / 2, focus.getY() - map.FOCUS_HEIGHT() / 2, map.FOCUS_WIDTH(), map.FOCUS_HEIGHT()));

         Pointt miniDisplay;

         //Plot main building
         miniDisplay = mainBuilding.miniPosition();
         a.fill(new Rectangle2D.Double(miniDisplay.getX() - MainBuilding.REP_SIZE, miniDisplay.getY() - MainBuilding.REP_SIZE, 2 * MainBuilding.REP_SIZE, 2 * MainBuilding.REP_SIZE));

         //Plot puck
         if (!puck.dead()) {
            miniDisplay = puck.miniPosition();
            Color[] colors = {Color.WHITE, Color.BLACK};
            float[] frac = {0.5f, 0.8f};
            RadialGradientPaint gp1 = new RadialGradientPaint(new Point((int) miniDisplay.getX(), (int) miniDisplay.getY()), REP_SIZE, frac, colors, MultipleGradientPaint.CycleMethod.NO_CYCLE);
            a.setPaint(gp1);
            a.fill(new Ellipse2D.Double(miniDisplay.getX() - REP_SIZE, miniDisplay.getY() - REP_SIZE, 2 * REP_SIZE, 2 * REP_SIZE));
         }

         //Plot enemies
         synchronized (enemies) {
            for (LivingUnit current : enemies) {
               if (current.dead()) {
                  continue;
               }
               a.setPaint(current.color());
               miniDisplay = current.miniPosition();
               a.fill(new Ellipse2D.Double(miniDisplay.getX() - REP_SIZE, miniDisplay.getY() - REP_SIZE, 2 * REP_SIZE, 2 * REP_SIZE));

            }
         }
      }
   }

   private void clearSelected() {
      synchronized (selectedUnits) {
         for (LivingUnit current : selectedUnits) {
            current.setSelected(false, null);
         }
      }
      selectedUnits.clear();
   }

   private void clearStaticSkills() {
      Archon.resetEncircle();
   }

   private void resetBonus() {
      puck.resetBonus();
      rbBonus[puck.bonusType()].setSelected(true);
      for (int i = 0; i < rbSecondSkill.length; i++) {
         rbSecondSkill[i].setEnabled(true);
      }
      for (int i = 0; i < rbUltimate.length; i++) {
         rbUltimate[i].setEnabled(true);
      }

      if (puck.bonusType() == Puck.SKILL_BONUS) {
         rbSecondSkill[puck.secondSkill().bonusType()].setSelected(true);
      }

      if (puck.bonusType() == Puck.SKILL_BONUS) {
         rbUltimate[puck.ultimate().bonusType()].setSelected(true);
      }
   }

   protected void endGame() {
      clearSelected();
      clearStaticSkills();
      resetBonus();
      if (paused) {
         Clocks.masterClock.run();
         paused = false;
      }
      Clocks.masterClock.reset();

      markers.clear();
      enemies.clear();
      visualEffects.clear();
      puck = null;
      mainBuilding = null;
   }

//Public Setter & Getter
   public Puck puck() {
      return puck;
   }

   public HashSet<LivingUnit> enemies() {
      return enemies;
   }

   public Pointt focus() {
      return focus;
   }

   public MainBuilding mainBuilding() {
      return mainBuilding;
   }

   //Protected Getter & Setter
   protected void setPuck(Puck puck) {
      this.puck = puck;
   }

   protected SkillCoolDown bFirstSkill() {
      return bFirstSkill;
   }

   protected SkillCoolDown bSecondSkill() {
      return bSecondSkill;
   }

   protected SkillCoolDown bThirdSkill() {
      return bThirdSkill;
   }

   protected SkillCoolDown bUltimate() {
      return bUltimate;
   }

   protected int processorID() {
      return processorID;
   }

   protected void setProcessorID(int processorID) {
      this.processorID = processorID;
   }

   protected int graphicsID() {
      return graphicsID;
   }

   protected void setGraphicsID(int graphicsID) {
      this.graphicsID = graphicsID;
   }

   protected void setFocus(Pointt focus) {
      this.focus = focus;
   }

   protected Board board() {
      return board;
   }

   protected MiniMap miniMap() {
      return miniMap;
   }

   protected void setMainBuilding(MainBuilding mainBuilding) {
      this.mainBuilding = mainBuilding;
   }

   public static boolean soundOn() {
      return soundOn;
   }

   public static boolean backGroundMusicOn() {
      return backGroundMusicOn;
   }

   public HashSet<UniversalEffect> visualEffects() {
      return visualEffects;
   }

   protected MainScreen welcomeScreen() {
      return welcomeScreen;
   }

   public void setMacros(Macros macros) {
      setupInputs(macros);
   }

   public byte difficulty() {
      return welcomeScreen.difficulty();
   }

   public Macros macros() {
      return welcomeScreen.macros();
   }

   public MessageText infoMessage() {
      return infoMessage;
   }

   public HashSet<Marker> markers() {
      return markers;
   }
}