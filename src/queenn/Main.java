/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package queenn;

import jade.core.AID;
import jade.core.Agent;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Nora
 */
public class Main extends Agent {

    static int queens;
    AID[] queenAIDs;

    @Override
    protected void takeDown() {
        super.takeDown(); //To change body of generated methods, choose Tools | Templates.
        System.out.println("Agent " + getAID().getLocalName() + " is terminating....");
    }

    @Override
    protected void setup() {
        try {
            Object[] args = getArguments();
            if (args.length > 0 && args != null) {
                queens = Integer.parseInt((String) args[0]);
                initQueens();
            } else {
                System.out.println("Sorry, wrong arguments for the first queen...");
                takeDown();
            }
            System.out.println("Main is now up and running");
          } catch (StaleProxyException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }

    }



    private void initQueens() throws StaleProxyException {
        if (queens < 25 && queens > 3) {
            ContainerController cc = getContainerController();
                    AgentController ac = null;
            for (int i = 0; i < queens; i++) {
                String queenName = "Queen" + i;
                Object[] arg = new Object[2];
                arg[0] = i;
                arg[1]=queens;
                ac = cc.createNewAgent(queenName, QueenN.class.getName(), arg);
                ac.start();
            }
            
        } else {
            System.out.println("Sorry, the application does not support " + queens + " queens for the N-queen problem");
            takeDown();
        }
    }

}
    

   

    

