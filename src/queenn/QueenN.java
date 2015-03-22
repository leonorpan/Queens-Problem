/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package queenn;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.SearchConstraints;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Nora
 */
public class QueenN extends Agent {

    private int id;
    private int total;
    AID nextQueen;
    AID previousQueen;
    int step;

    @Override
    protected void setup() {
        try {

            super.setup(); //To change body of generated methods, choose Tools | Templates.
            Object[] args = getArguments();
            if (args.length > 0 && args != null) {
                id = (Integer) args[0];
                total = (Integer) args[1];
            }
            registerQueenService();
            initializeStep();
            System.out.println("Queen agent: " + id + " is now up and running");
            SearchQueens search=new SearchQueens(getPrevious(), getNext());
            addBehaviour(search);
        } catch (FIPAException ex) {
            Logger.getLogger(QueenN.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InterruptedException ex) {
            Logger.getLogger(QueenN.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void registerQueenService() throws FIPAException {
        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName(getAID());
        ServiceDescription sd = new ServiceDescription();
        sd.setName("queen" + id);
        sd.setType("queen");
        sd.addLanguages("English");
        dfd.addServices(sd);
        DFService.register(this, dfd);
        System.out.println("Queen " + getAID().getLocalName() + " service " + sd.getName() + " is registered");
    }

    private void initializeStep() throws InterruptedException {
        if (id == 0) {
            step = 0;
        } else {
            step = 1;
        }
    }

    private int getPrevious() {
        if (id == 0) {
            step = 0;
            return total - 1;
        } else {
            step = 1;
            return id - 1;
        }
    }

    private int getNext() {
        if (id == (total - 1)) {
            return 0;
        } else {
            return id + 1;
        }
    }


    public class PlaceQueens  extends Behaviour{

        int[] table;
        List<Integer> tried = new ArrayList<>();

        
        @Override
        public void action() {
            switch (step) {
                //step for the first queen, should not be applied for the other queens
                case (0):
                    //select a random position
                    table = selectRandomPosition(new int[total]);
                    ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
                    msg.setContent(Arrays.toString(table));
                    msg.setSender(myAgent.getAID());
                    msg.addReceiver(nextQueen);
                    System.out.println("Message sent : " + Arrays.toString(table) + " from " + msg.getSender().getLocalName() + " which is the first message");
                    send(msg);
                    step = 1;
                    break;
                case (1):
                    //ama lavw minima pisw, tote simainei oti prepei na allaksw thesh
                    ACLMessage message = myAgent.receive();
                    if (message != null) {
                        String content = message.getContent();
                        if (message.getPerformative() == ACLMessage.FAILURE) {
                            table = putQueen(toIntArray(content));
                            System.out.println("Queen: " + id + " Message received from: "
                                    + message.getSender().getLocalName()
                                    + " is: " + content + " with failure");
                            break;
                        } else if (message.getPerformative() == ACLMessage.INFORM) {
                            int[] receivedArray=toIntArray(content);
                            table = putQueen(receivedArray);
                            System.out.println("Queen: " + id + " Message received from: "
                                    + message.getSender().getLocalName()
                                    + " is: " + content + " with inform");
                            break;
                        }else if (message.getPerformative()==ACLMessage.ACCEPT_PROPOSAL){
                            table=toIntArray(content);
                            step=4;
                            System.out.println("Queen: " + id + " Message received from: "
                                    + message.getSender().getLocalName()
                                    + " is: " + content + " with accept");
                            break;
                        } else {
                            block();
                        }

                    } else {
                        block();
                    }
                    break;
                case (2):
                    //stelnw ton neo table me tin nea mou thesi stin epomenh vsasilissa
                    ACLMessage main = new ACLMessage(ACLMessage.INFORM);
                    main.setContent(Arrays.toString(table));
                    main.setSender(myAgent.getAID());
                    main.addReceiver(nextQueen);
                    System.out.println("Message sent : " + Arrays.toString(table) + " from " + main.getSender().getLocalName());
                    send(main);
                    step = 1;
                    break;

                //send a fail message to the previous queen. this message is received in step 1
                case (3):
                    ACLMessage fail = new ACLMessage(ACLMessage.FAILURE);
                    table[id]=0;
                    fail.setContent(Arrays.toString(table));
                    fail.setConversationId("init");
                    fail.setSender(myAgent.getAID());
                    fail.addReceiver(previousQueen);
                    System.out.println("Message sent : " + Arrays.toString(table) + " from " + fail.getSender().getLocalName());
                    tried.clear();
                    send(fail);
                    step = 1;
                    break;
                case (4):
                    if (id>0){
                    ACLMessage win = new ACLMessage(ACLMessage.ACCEPT_PROPOSAL);
                    win.setContent(Arrays.toString(table));
                    win.setSender(myAgent.getAID());
                    win.addReceiver(previousQueen);
                    System.out.println("Message sent : " + Arrays.toString(table) + " from " + win.getSender().getLocalName());
                    send(win);
                    }else {
                        System.out.println("FINISH!");
                    }
                    step = 5;
                    break;
                case (5):
                    step=6;
                    break;
            }
        }
        
        public  int[] toIntArray(String input) {
    String beforeSplit = input.replaceAll("\\[|\\]|\\s", "");
    String[] split = beforeSplit.split("\\,");
    int[] result = new int[split.length];
    for (int i = 0; i < split.length; i++) {
        result[i] = Integer.parseInt(split[i]);
    }
    return result;
}

        @Override
        public boolean done() {
            if (step==6){
                takeDown();
                return true;
            }
            return false;
        }

        private int[] putQueen(int[] arr) {
            if ((id == 0)&&((arr[id]!=0)&&(arr[id+1]!=0))) {
                step = 4;
                printQueens(arr);
                return arr;
            }
            for (int i = 0; i < total; i++) {
                arr[id] = i;
                if (isConsistent(arr, id)&&(!tried.contains(i))) {
                    step = 2;
                    tried.add(i);
                    return arr;
                }
            }
            step = 3;
            return arr;
        }

        private void printQueens(int[] q) {
            int N = q.length;
            for (int i = 0; i < N; i++) {
                for (int j = 0; j < N; j++) {
                    if (q[i] == j) {
                        System.out.print("Q ");
                    } else {
                        System.out.print("* ");
                    }
                }
                System.out.println();
            }
            System.out.println();
            step = 4;
        }

        public boolean isConsistent(int[] q, int k) {
            for (int i = 0; i < k; i++) {
                if (q[i] == q[k]) {
                    return false;   // same column
                }
                if ((q[i] - q[k]) == (k - i)) {
                    return false;   // same major diagonal
                }
                if ((q[k] - q[i]) == (k - i)) {
                    return false;   // same minor diagonal
                }
            }
            return true;
        }
        
        private void printArray(int[] a){
            for (int i =0; i<a.length; i++){
                System.out.print(a[i]+" ");
            }
        }
        
        

        private int[] changePosition(int[] received) {
            int[] newRandomPosition;
            do {
                newRandomPosition = selectRandomPosition(received);
                
            } while (!tried.contains(received[id]));
            tried.add(newRandomPosition[id]);
            return newRandomPosition;
        }


        private int[] selectRandomPosition(int[] positions) {
            Random r = new Random();
            int randomPosition = r.nextInt(total);
            positions[id] = randomPosition;
            if (id==0){
            tried.add(randomPosition);
            }
            return positions;
        }

    }

    @Override
    protected void takeDown() {
        System.out.println("Queen " + getAID().getLocalName() + " is terminating.");

    }

    /**
     * @param args the command line arguments
     */
    private class SearchQueens extends Behaviour {

        int previous;
        int next;

        public SearchQueens(int pre, int succ) {
            this.previous = pre;
            this.next = succ;
        }

        @Override
        public void action() {
            try {
                // Build the description used as template for the search
                DFAgentDescription template = new DFAgentDescription();
                ServiceDescription templateSd = new ServiceDescription();
                templateSd.setType("queen");
                template.addServices(templateSd);

                SearchConstraints sc = new SearchConstraints();
                // We want to receive 10 results at most
                sc.setMaxResults(new Long(total));
                DFAgentDescription[] results = DFService.search(getAgent(), template, sc);
                if (results.length > 0) {
                    System.out.println("Queen " + getLocalName() + " found the following queens:");
                    for (int i = 0; i < results.length; ++i) {
                        DFAgentDescription dfd = results[i];
                        AID provider = dfd.getName();
                        // The same agent may provide several services; we are only interested
                        // in the weather-forcast one
                        Iterator it = dfd.getAllServices();
                        while (it.hasNext()) {
                            ServiceDescription sd = (ServiceDescription) it.next();
                            if (sd.getName().equals("queen" + previous)) {
                                previousQueen = provider;
                                //System.out.println("Queen: " + myAgent.getLocalName() + " 's predecessor is:" + previousQueen.getLocalName());
                            } else if (sd.getName().equals("queen" + next)) {
                                nextQueen = provider;
                                //System.out.println("Queen: " + myAgent.getLocalName() + " 's successor is:" + nextQueen.getLocalName());
                            }
                        }
                    }
                } else {
                    System.out.println("Queen " + getLocalName() + " did not find any queens");
                }
            } catch (FIPAException fe) {
                fe.printStackTrace();
            }
        }

        @Override
        public boolean done() {
            if ((previousQueen != null) && (nextQueen != null)) {
                myAgent.addBehaviour(new PlaceQueens());
                return true;
            }
            return false;
        }

    }
}
