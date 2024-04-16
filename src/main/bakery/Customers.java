package bakery;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EmptyStackException;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Stack;

import util.CardUtils;

import bakery.CustomerOrder.CustomerOrderStatus;

/**
 * Manages the collection of customer orders within a bakery system, handling both active and inactive orders.
 * This class facilitates the initialization, activation, and management of customer orders based on a deck loaded from a file.
 * It supports operations such as adding new customer orders to the active list, removing customers, and checking the status of the order list.
 * Customers are managed in a way that simulates a real-world scenario where customers can become impatient or leave, and new customers can be added from a pre-defined deck.
 *
 * Usage involves initializing the class with a specified deck file and random seed for shuffling, and subsequently managing customer orders through various methods that simulate business operations like adding and removing orders, and transitioning orders between active and inactive states.
 *
 * @author Adam Aly
 * @version 1.0
 * @see CustomerOrder
 * @see Layer
 */
public class Customers implements java.io.Serializable {
    private Collection<CustomerOrder> activeCustomers;
    private Collection<CustomerOrder> customerDeck;
    private List<CustomerOrder> inactiveCustomers;
    private Random random;

    private static final long serialVersionUID = 11085168;


    /**
     * Initializes a new Customers instance with a specified deck file and number of players.
     * Loads customer orders from the specified deck file and initializes the order collections based on the number of players.
     *
     * @param deckFile the path to the file containing the deck of customer orders.
     * @param random a Random object for shuffling the customer deck.
     * @param layers a collection of layers to be used in initializing customer orders.
     * @param numPlayers the number of players in the game, influencing initial order setup.
     * @throws FileNotFoundException if the deck file cannot be found.
     */
    public Customers(String deckFile, Random random, Collection<Layer> layers, int numPlayers) throws FileNotFoundException {
        this.random = random;
        initialiseCustomerDeck(deckFile, layers, numPlayers);
        activeCustomers = new ArrayList<>();
        for(int i = 0; i < 3; i++) {
            activeCustomers.add(null);
        }
        //activeCustomers.add(null);
        // should the below be done in startGame()?
        // if(numPlayers % 2 == 0) {
        //     activeCustomers.add(drawCustomer());
        // } else {
        //     activeCustomers.add(drawCustomer());
        //     activeCustomers.add(drawCustomer());
        // }
        inactiveCustomers = new ArrayList<>();
    }

   /**
     * Adds a new customer order to the active customer list from the preloaded customer deck. This method is typically
     * called to process and activate new orders at regular intervals or specific triggers within the system.
     * If the customer deck is not empty, it draws the next order and adds it to the active list. If adding a new customer
     * causes the list to exceed its standard capacity, it may trigger other customers to leave soon, based on the implemented logic.
     *
     * @return the newly activated CustomerOrder if the deck is not empty, or null if it is empty, indicating no new customers can be added.
     * @throws EmptyStackException if the customer deck is empty, indicating that no more orders are available to draw.
     */
    public CustomerOrder addCustomerOrder() throws EmptyStackException {
        CustomerOrder c = timePasses();
        if(!customerDeck.isEmpty()) {
            ((LinkedList<CustomerOrder>) activeCustomers).add(0, drawCustomer());
            if(c != null && size() == 3) {
                customerWillLeaveSoon();
            }
        } else {
            throw new EmptyStackException();
        }
        return c;
    }

    /**
     * Evaluates if any customer will soon leave due to waiting too long. This method should be called regularly to manage
     * customer patience and to handle transitions of customers from active to potentially leaving.
     *
     * @return true if there is a customer who will soon leave, false otherwise.
     */
    public boolean customerWillLeaveSoon() {
        if(!isEmpty()) {
            if(activeCustomers.size() > 2) {
                if(customerDeck.size() == 0 && activeCustomers.toArray()[1] != null && activeCustomers.toArray()[2] != null) {
                    ((CustomerOrder) activeCustomers.toArray()[2]).setStatus(CustomerOrderStatus.IMPATIENT);
                    return true;
                } else if(customerDeck.size() == 0 && activeCustomers.toArray()[0] == null && activeCustomers.toArray()[1] == null && activeCustomers.toArray()[2] != null) {
                    ((CustomerOrder) activeCustomers.toArray()[2]).setStatus(CustomerOrderStatus.IMPATIENT);
                    return true;
                } else if(customerDeck.size() == 0 && activeCustomers.toArray()[0] == null && activeCustomers.toArray()[1] != null && activeCustomers.toArray()[2] != null) {
                    ((CustomerOrder) activeCustomers.toArray()[2]).setStatus(CustomerOrderStatus.IMPATIENT);
                    return true;
                } else if(activeCustomers.toArray()[2] != null && activeCustomers.toArray()[1] != null && activeCustomers.toArray()[0] != null) {
                    ((CustomerOrder) activeCustomers.toArray()[2]).setStatus(CustomerOrderStatus.IMPATIENT);
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Draws the next customer from the customer deck to become active. If the customer deck is empty, this method will handle
     * the situation appropriately, possibly by reshuffling inactive customers into the deck.
     *
     * @return the CustomerOrder drawn to become an active customer, or null if no more customers can be drawn.
     */
    public CustomerOrder drawCustomer() {
        if(!customerDeck.isEmpty()) {
            CustomerOrder customerOrder = ((Stack<CustomerOrder>) customerDeck).pop();
            return customerOrder;
        }
        return null;
    }

    /**
     * Retrieves a collection of all currently active customers.
     *
     * @return a Collection of CustomerOrder objects representing all active customers.
     */
    public Collection<CustomerOrder> getActiveCustomers() {
        return activeCustomers;
    }

    /**
     * Provides access to the internal deck of customers which includes both active and inactive customers waiting to be drawn.
     *
     * @return a Collection of CustomerOrder objects representing the customer deck.
     */
    public Collection<CustomerOrder> getCustomerDeck() {
        return customerDeck;
    }

    /**
     * Retrieves all customers whose orders can potentially be fulfilled based on the current availability of ingredients or resources.
     *
     * @param hand a List of Ingredient objects representing the current resources available to the player.
     * @return a Collection of CustomerOrder objects that can potentially be fulfilled.
     */
    public Collection<CustomerOrder> getFulfilable(List<Ingredient> hand) {
        Collection<CustomerOrder> result = new ArrayList<>();
        for (CustomerOrder customerOrder : activeCustomers) {
            if(customerOrder != null && customerOrder.canFulfill(hand)) {
                result.add(customerOrder);
            }
        }
        return result;
    }

    /**
     * Fetches all inactive customers that match a specific order status.
     * This is useful for processing or displaying orders that have been abandoned or are in a specific state.
     *
     * @param status the CustomerOrderStatus to filter the inactive customers by.
     * @return a Collection of CustomerOrder objects that are inactive and match the given status.
     */
    public Collection<CustomerOrder> getInactiveCustomersWithStatus(CustomerOrderStatus status) {
        Collection<CustomerOrder> result = new ArrayList<>();
        for(CustomerOrder customerOrder : inactiveCustomers) {
            if (customerOrder.getStatus().equals(status)) {
                result.add(customerOrder);
            }
        }
        return result;
    }
    
    /**
     * Initializes the customer deck from a specified file and arranges the deck based on the number of players.
     * Different numbers of players require different arrangements of customer orders based on their level to ensure game balance.
     * This method reads customer orders from the file, categorizes them by level, and then selectively adds them to the customer deck based on game rules.
     *
     * @param deckFile the path to the file containing customer orders.
     * @param layers a collection of layers available for initializing customer orders.
     * @param numPlayers the number of players, which affects the distribution of customer orders.
     * @throws FileNotFoundException if the specified deck file cannot be found.
     */
    private void initialiseCustomerDeck(String deckFile, Collection<Layer> layers, int numPlayers) throws FileNotFoundException {
        /*
         * 2 players: x4 Level 1, x2 Level 2, x1 Level 3
         * 3&4 players: x1 Level 1, x2 Level 2, x4 Level 3
         * 5 players: x1 Level 2, x6 Level 3
         */
        customerDeck = new Stack<>();
        List<CustomerOrder> tempCustomerDeck = new ArrayList<>();
        try {
            tempCustomerDeck.addAll(CardUtils.readCustomerFile(deckFile, layers));
        } catch (FileNotFoundException e) {
            throw new FileNotFoundException();
        }
        Collections.shuffle(tempCustomerDeck, random);
        
        ArrayList<CustomerOrder> level1CustomerOrders = new ArrayList<>();
        ArrayList<CustomerOrder> level2CustomerOrders = new ArrayList<>();
        ArrayList<CustomerOrder> level3CustomerOrders = new ArrayList<>();
        
        for(CustomerOrder customerOrder : tempCustomerDeck) {
            if(customerOrder.getLevel() == 1) {
                level1CustomerOrders.add(customerOrder);
            } else if(customerOrder.getLevel() == 2) {
                level2CustomerOrders.add(customerOrder);
            } else if(customerOrder.getLevel() == 3) {
                level3CustomerOrders.add(customerOrder);
            }
        }

        // Collections.shuffle(level1CustomerOrders, random);
        // Collections.shuffle(level2CustomerOrders, random);
        // Collections.shuffle(level3CustomerOrders, random);

        switch(numPlayers) {
            case 2:
                customerDeck.add(level1CustomerOrders.get(0));
                customerDeck.add(level1CustomerOrders.get(1));
                customerDeck.add(level1CustomerOrders.get(2));
                customerDeck.add(level1CustomerOrders.get(3));
                customerDeck.add(level2CustomerOrders.get(0));
                customerDeck.add(level2CustomerOrders.get(1));
                customerDeck.add(level3CustomerOrders.get(0));
                break;
            case (3):
                customerDeck.add(level1CustomerOrders.get(0));
                customerDeck.add(level2CustomerOrders.get(0));
                customerDeck.add(level2CustomerOrders.get(1));
                customerDeck.add(level3CustomerOrders.get(0));
                customerDeck.add(level3CustomerOrders.get(1));
                customerDeck.add(level3CustomerOrders.get(2));
                customerDeck.add(level3CustomerOrders.get(3));
                break;
            case 4:
                customerDeck.add(level1CustomerOrders.get(0));
                customerDeck.add(level2CustomerOrders.get(0));
                customerDeck.add(level2CustomerOrders.get(1));
                customerDeck.add(level3CustomerOrders.get(0));
                customerDeck.add(level3CustomerOrders.get(1));
                customerDeck.add(level3CustomerOrders.get(2));
                customerDeck.add(level3CustomerOrders.get(3));
                break;
            case 5:
                customerDeck.add(level2CustomerOrders.get(0));
                customerDeck.add(level3CustomerOrders.get(0));
                customerDeck.add(level3CustomerOrders.get(1));
                customerDeck.add(level3CustomerOrders.get(2));
                customerDeck.add(level3CustomerOrders.get(3));
                customerDeck.add(level3CustomerOrders.get(4));
                customerDeck.add(level3CustomerOrders.get(5));
                break;
        }
        Collections.shuffle((Stack<CustomerOrder>) customerDeck, random);
    }

    /**
     * Checks if there are no active customers.
     *
     * @return true if there are no customers currently active, false otherwise.
     */
    public boolean isEmpty() {
        for(CustomerOrder customerOrder : activeCustomers) {
            if(customerOrder != null) {
                return false;
            }
        }
        return true;
    }

    /**
     * Retrieves the last active customer order without removing it from the active list.
     * This method is typically used to preview the next customer to be processed without altering the queue.
     *
     * @return the last CustomerOrder in the active queue, or null if there are no active customers.
     */
    public CustomerOrder peek() {
        if(isEmpty()) {
            return null;
        }
        if(activeCustomers.size() > 1) {
            return ((LinkedList<CustomerOrder>) activeCustomers).getLast();
        }
        return null;
    }

    /**
     * Removes a specified customer order from the active customers and moves it to the inactive list, marking it as given up.
     * This method is used to manage customers who are no longer active in the system.
     *
     * @param customer the CustomerOrder to be removed and marked as inactive.
     */
    public void remove(CustomerOrder customer) {
        
        int customerIndex = ((LinkedList<CustomerOrder>) activeCustomers).indexOf(customer);
        ((LinkedList<CustomerOrder>) activeCustomers).set(customerIndex, null);
        
        //activeCustomers.remove(customer);
        inactiveCustomers.add(customer);
        customer.setStatus(CustomerOrderStatus.GIVEN_UP);
    }

    /**
     * Counts the number of active customers.
     *
     * @return the number of active customers.
     */
    public int size() {
        int result = 0;
        for(CustomerOrder customerOrder : activeCustomers) {
            if(customerOrder != null) {
                result++;
            }
        }
        return result;
    }

    /**
     * Simulates the passing of time, affecting customer patience and potentially causing the next customer in line to leave.
     * This method should be used in each cycle of the game or system operation to update customer statuses.
     *
     * @return the CustomerOrder of the customer who leaves due to impatience, or null if all customers are patient.
     */
    public CustomerOrder timePasses() {
        LinkedList<CustomerOrder> activeCustomerDeck = new LinkedList<>(activeCustomers);
        CustomerOrder leavingCustomer = null;
        customerWillLeaveSoon();
        
        // Check if the customerDeck is empty
        if (customerDeck.isEmpty()) {
            if (activeCustomerDeck.size() >= 3) {
                if(activeCustomerDeck.get(0) != null && activeCustomerDeck.get(1) != null && activeCustomerDeck.get(2) != null) {
                    activeCustomerDeck.add(0, null);
                    leavingCustomer = activeCustomerDeck.removeLast();
                } else if(activeCustomerDeck.get(0) != null && activeCustomerDeck.get(1) != null && activeCustomerDeck.get(2) == null) {
                    leavingCustomer = activeCustomerDeck.removeLast();
                    activeCustomerDeck.add(0, null);
                } else if(activeCustomerDeck.get(0) != null && activeCustomerDeck.get(1) == null && activeCustomerDeck.get(2) != null) {
                    activeCustomerDeck.set(1, activeCustomerDeck.get(0));
                    activeCustomerDeck.set(0, null);
                } else if(activeCustomerDeck.get(0) == null && activeCustomerDeck.get(1) != null && activeCustomerDeck.get(2) != null) {
                    leavingCustomer = activeCustomerDeck.removeLast();
                    activeCustomerDeck.add(0, null);
                } else {
                    leavingCustomer = activeCustomerDeck.removeLast();
                    activeCustomerDeck.addFirst(null);
                }
            }
        } else {
            // Start from the rightmost position and shift every card to the right until you find an empty space
            if(activeCustomerDeck.size() >= 3) {
                if(activeCustomerDeck.get(0) == null) {
                    activeCustomerDeck.remove(0);
                } else if(activeCustomerDeck.get(1) == null) {
                    activeCustomerDeck.set(1, activeCustomerDeck.get(0));
                    activeCustomerDeck.remove(0);
                } else if(activeCustomerDeck.get(2) == null) {
                    activeCustomerDeck.set(2, activeCustomerDeck.get(1));
                    activeCustomerDeck.set(1, activeCustomerDeck.get(0));
                    activeCustomerDeck.remove(0);
                } else {
                    leavingCustomer = activeCustomerDeck.removeLast();
                }
            } else {
                //activeCustomerDeck.addLast(null);
            }
        }
        if(leavingCustomer != null) {
            inactiveCustomers.add(leavingCustomer);
            leavingCustomer.setStatus(CustomerOrderStatus.GIVEN_UP);
        }
        
        activeCustomers = activeCustomerDeck;
        return leavingCustomer;
    }

}
