package org.dreambot.raktak;

import org.dreambot.api.methods.Calculations;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.script.AbstractScript;
import org.dreambot.api.script.Category;
import org.dreambot.api.script.ScriptManifest;
import org.dreambot.api.utilities.Timer;
import org.dreambot.api.utilities.impl.Condition;
import org.dreambot.api.wrappers.interactive.GameObject;
import org.dreambot.api.wrappers.interactive.NPC;
import org.dreambot.api.wrappers.widgets.message.Message;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.IOException;
import java.net.URL;

@ScriptManifest(author = "Raktak", category = Category.WOODCUTTING, description ="Test", name ="First Script" , version = 1.0)
public class Main extends AbstractScript {
    private Area bankArea = new Area(3092, 3240, 3097, 3246, 0);
    private Area treeArea = new Area(3091, 3290, 3072, 3310, 0);
    private boolean startScript;
    private BasicWoodcutterGUI gui;
    private Image mainPaint = getImage("https://i.imgur.com/0GQ9s5R.png");
    private int logsCut;
    private Timer timeRan;

    @Override
    public void onStart() {
        gui = new BasicWoodcutterGUI(this);
        gui.setVisible(true);
        timeRan = new Timer();
    }

    @Override
    public void onMessage(Message message) {
        if(message.getMessage().contains("You get")){
            logsCut++;
        }
    }

    @Override
    public int onLoop() {
        /**
         * Chopping trees: Time to chop some trees, our inventory isn't full. We want to fill it up.
         */
        if(startScript) {
            if (!getInventory().isFull()) {
                if (treeArea.contains(getLocalPlayer())) {
                    chopTree(gui.getTreeType()); //change "Tree" to the name of your tree.
                } else {
                    if (getWalking().walk(treeArea.getRandomTile())) {
                        sleep(Calculations.random(3000, 5500));
                    }
                }
            }

            /**
             * Banking: Time to bank our logs. Our inventory is full, we want to empty it.
             */
            if (getInventory().isFull()) { //it is time to bank
                if (bankArea.contains(getLocalPlayer())) {
                    bank();
                } else {
                    if (getWalking().walk(bankArea.getRandomTile())) {
                        sleep(Calculations.random(3000, 6000));
                    }
                }
            }
        }
        return 200; //Sleeps for 200 milliseconds (0.2 seconds)
    }

    @Override
    public void onPaint(Graphics2D g) {
        Font font = new Font("Times new roamn", Font.PLAIN, 19);
        g.setFont(font);
        g.setColor(Color.BLACK);

        g.drawImage(mainPaint, 0, 340, null);

        g.drawString("" + timeRan.formatTime(), 121, 371);

        g.drawString("" + logsCut, 121, 400);

        g.drawString("" + logsCut * (int)(3600000D / (timeRan.elapsed())),121, 430);
    }


    /**
     *  --------Methods--------
     */

    /**
     * ****************************************************************************************
     * Function:	        chopTree
     * Description: 	    A simple way to interact with a tree gameObject.
     * @param nameOfTree    The name of the tree we want to cut down.
     * ****************************************************************************************
     */
    private void chopTree(String nameOfTree){
        GameObject tree = getGameObjects().closest(gameObject -> gameObject != null && gameObject.getName().equals(nameOfTree));
        if(tree != null && tree.interact("Chop down")){
            log("Sleeping for tree");
            sleepUntil(new Condition() {
                int animationCount = 0;
                int sleepTimer = Calculations.random(300,550);

                public boolean verify() {
                    if (getLocalPlayer().getAnimation() != -1) { //We're actually animating with the tree.
                        animationCount = 0; //Reset the count back to 0
                    } else { //We're sitting still, or lagging-out
                        animationCount++; //Add 1 to the count. If we go over our sleepTimer count, it's time to move on, or re-interact.
                    }
                    return !tree.exists() || animationCount > sleepTimer || getDialogues().canContinue(); //Will kick out of the sleep method if we're idle for over 7-10 seconds and the tree is still present
                }
            },100000);
            log("Done sleeping for tree");
        }
    }

    /**
     * ****************************************************************************************
     * Function:	    bank
     * Description: 	A simple method which finds the nearest banker NPC to the local
     *                  player, interacts with it, and sleeps until the bank is open, then
     *                  precedes to deposit all items except for the axe.
     * ****************************************************************************************
     */
    private void bank(){
        NPC banker = getNpcs().closest(npc -> npc != null && npc.hasAction("Bank"));

        if(getBank().isOpen() || (banker != null && banker.interact("Bank") && sleepUntil(() -> getBank().isOpen(), 9000))){ //The bank is open, or we interact with the banker and sleep till open
            if(getInventory().onlyContains(item1 -> item1 != null && item1.getName().contains(" axe")) || getBank().depositAllExcept(item -> item != null && item.getName().contains(" axe"))){
                if(sleepUntil(() -> !getInventory().isFull(), Calculations.random(4500, 8000))){
                    if(getBank().close()){
                        sleepUntil(() -> !getBank().isOpen(), Calculations.random(4500, 8000));
                    }
                }
            }
        }
    }

    /**
     * ****************************************************************************************
     * Function:	    getImage
     * Description: 	Method which returns an image from a given URL link.
     * ****************************************************************************************
     */
    private Image getImage(String url){
        try {
            return ImageIO.read(new URL(url));
        }catch (IOException e){
            return null;
        }
    }



    /**
     *  --------Getters/Setters--------
     */
    public void setStartScript(boolean startScript) {
        this.startScript = startScript;
    }
}

