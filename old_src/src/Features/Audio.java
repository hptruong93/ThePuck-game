package Features;

import Main.Game;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

public class Audio implements Runnable {

   private static ScheduledThreadPoolExecutor backGroundMusic;
   private static final String DIRECTORY_DEFAULT = "file:./Sounds/";
   public static final String BACK_GROUND = "BackGround.aiff";
   public static final String DIE = "Die.wav";
   public static final String ENCIRCLE = "Encircle.wav";
   public static final String NIGHT_SILENCE = "Silence.wav";
   public static final String VENOM = "Venom.wav";
   public static final String ILLUSORY_DIVE = "IllusoryDive.wav";
   public static final String SOUL_DIVE = "SoulDive.wav";
   public static final String SOUL_LINK = "SoulLink.wav";
   public static final String SOUL_LINK_APPROACH = "SoulLinkApproach.wav";
   public static final String PATH_OF_VENOMS = "PathOfVenoms.wav";
   public static final String SPIDERNET_THROW = "SpiderNet.wav";
   public static final String NET_SHIELD = "NetShield.wav";
   public static final String SHADOW_DANCE = "ShadowDance.wav";
   public static final String SUICIDAL_ATTACK = "SuicidalAttack.wav";
   public static final String BOUND_OF_FREEDOM = "BoundOfFreedom.wav";
   public static final String EXPONENTIAL_GROWTH = "ExponentialGrowth.wav";
   public static final String FREEZING_PROJECTILE = "FreezingProjectile.wav";
   public static final String MOON_BLADE_START = "MoonBladeStart.wav";
   public static final String MOON_BLADE_LOOP = "MoonBladeLoop.wav";
   public static final String MOON_WALK = "MoonWalk.wav";
   public static final String BLAZE = "Blaze.wav";
   public static final String FIRST_SKILL = "FirstSkill.wav";
   public static final String SECOND_SKILL = "SecondSkill.wav";
   public static final String THIRD_SKILL = "ThirdSkill.wav";
   public static final String ULTIMATE = "Ultimate.wav";
   public static final String TOGGLE = "Toggle.wav";
   public static final String TOGGLE2 = "Toggle2.wav";
   private static HashMap<String, Clip> clips;
   private Clip backGround;

   public Audio() {
      AudioInputStream inputStream = null;
      try {
         backGround = AudioSystem.getClip();

         URL soundLocation = new URL(DIRECTORY_DEFAULT + BACK_GROUND);
         inputStream = AudioSystem.getAudioInputStream(soundLocation);
         backGround.open(inputStream);
      } catch (UnsupportedAudioFileException | LineUnavailableException | IOException ex) {
         Logger.getLogger(Audio.class.getName()).log(Level.SEVERE, null, ex);
      } finally {
         try {
            inputStream.close();
         } catch (IOException ex) {
            Logger.getLogger(Audio.class.getName()).log(Level.SEVERE, null, ex);
         }
      }
   }

   public static void schedule() {
      backGroundMusic = new ScheduledThreadPoolExecutor(1);
      backGroundMusic.scheduleAtFixedRate(new Audio(), 0, 1, TimeUnit.SECONDS);
   }

   public static void clearTask() {
      backGroundMusic.shutdown();
      backGroundMusic = new ScheduledThreadPoolExecutor(1);
   }

   @Override
   public void run() {
      if (Game.backGroundMusicOn()) {
         if (!backGround.isRunning()) {
            backGround.loop(Clip.LOOP_CONTINUOUSLY);
         }
      } else {
         if (backGround.isRunning()) {
            backGround.stop();
            backGround.setFramePosition(0);
         }
      }
   }

   public static void initialize() {
      final String[] SOUND_COLLECTION = {DIE, ENCIRCLE, NIGHT_SILENCE, VENOM, ILLUSORY_DIVE,
         SOUL_DIVE, SOUL_LINK, SOUL_LINK_APPROACH, PATH_OF_VENOMS, SPIDERNET_THROW, NET_SHIELD, BOUND_OF_FREEDOM,
         SHADOW_DANCE, SUICIDAL_ATTACK, EXPONENTIAL_GROWTH, FREEZING_PROJECTILE, MOON_BLADE_START,
         MOON_BLADE_LOOP, BLAZE, MOON_WALK, FIRST_SKILL, SECOND_SKILL, THIRD_SKILL, ULTIMATE, TOGGLE, TOGGLE2};
      clips = new HashMap<>();
      for (int i = 0; i < SOUND_COLLECTION.length; i++) {
         try {
            AudioInputStream inputStream = null;
            URL soundLocation = new URL(DIRECTORY_DEFAULT + SOUND_COLLECTION[i]);
            inputStream = AudioSystem.getAudioInputStream(soundLocation);

            Clip currentClip = AudioSystem.getClip();
            currentClip.open(inputStream);
            clips.put(SOUND_COLLECTION[i], currentClip);
         } catch (LineUnavailableException ex) {
            Logger.getLogger(Audio.class.getName()).log(Level.SEVERE, null, ex);
            DebugFile.printLog(ex);
         } catch (UnsupportedAudioFileException ex) {
            Logger.getLogger(Audio.class.getName()).log(Level.SEVERE, null, ex);
            DebugFile.printLog(ex);
         } catch (IOException ex) {
            Logger.getLogger(Audio.class.getName()).log(Level.SEVERE, null, ex);
            DebugFile.printLog(ex);
         }

      }
   }

   public static void playSound(String soundToPlay) {
      if (!Game.soundOn()) {
         return;
      }

      Clip playing = clips.get(soundToPlay);
      playing.setFramePosition(0);
      playing.loop(0);
   }

   public static void attemptReplay(String soundToReplay) {
      if (!Game.soundOn()) {
         return;
      }

      Clip playing = clips.get(soundToReplay);
      if (!playing.isRunning()) {
         playing.setFramePosition(0);
         playing.loop(0);
      }
   }
}