package bakery;

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

public class Customers implements java.io.Serializable {
    private Collection<CustomerOrder> activeCustomers;
    private Collection<CustomerOrder> customerDeck;
    private List<CustomerOrder> inactiveCustomers;
    private Random random;

    private static final long serialVersionUID = 11085168;


    public Customers(String deckFile, Random random, Collection<Layer> layers, int numPlayers) {
        initialiseCustomerDeck(deckFile, layers, numPlayers);
        activeCustomers = new ArrayList<>();
        // should the below be done in startGame()?
        // if(numPlayers % 2 == 0) {
        //     activeCustomers.add(drawCustomer());
        // } else {
        //     activeCustomers.add(drawCustomer());
        //     activeCustomers.add(drawCustomer());
        // }
        inactiveCustomers = new ArrayList<>();
        this.random = random;
    }

    public CustomerOrder addCustomerOrder() {
        CustomerOrder c = timePasses();
        if(c != null) {
            inactiveCustomers.add(c);
            c.setStatus(CustomerOrderStatus.GIVEN_UP);
        }
        if(!customerDeck.isEmpty()) {
            ((LinkedList<CustomerOrder>) activeCustomers).add(0, drawCustomer());
        } else {
            throw new EmptyStackException();
        }
        return c;
    }

    public boolean customerWillLeaveSoon() {
        if(!isEmpty()) {
            if(activeCustomers.size() > 2) {
                if(activeCustomers.toArray()[2] != null) {
                    ((CustomerOrder) activeCustomers.toArray()[2]).setStatus(CustomerOrderStatus.IMPATIENT);
                    return true;
                }
            }
        }
        return false;
    }

    public CustomerOrder drawCustomer() {
        if(!customerDeck.isEmpty()) {
            CustomerOrder customerOrder = ((Stack<CustomerOrder>) customerDeck).pop();
            return customerOrder;
        }
        return null;
    }

    public Collection<CustomerOrder> getActiveCustomers() {
        return activeCustomers;
    }

    public Collection<CustomerOrder> getCustomerDeck() {
        if(customerDeck.size() < 1) {
            return null;
        }
        return customerDeck;
    }

    public Collection<CustomerOrder> getFulfilable(List<Ingredient> hand) {
        Collection<CustomerOrder> result = new ArrayList<>();
        for (CustomerOrder customerOrder : activeCustomers) {
            if (hand.containsAll(customerOrder.getRecipe())) {
                result.add(customerOrder);
            }
        }
        return result;
    }

    public Collection<CustomerOrder> getInactiveCustomersWithStatus(CustomerOrderStatus status) {
        Collection<CustomerOrder> result = new ArrayList<>();
        for(CustomerOrder customerOrder : inactiveCustomers) {
            if (customerOrder.getStatus().equals(status)) {
                result.add(customerOrder);
            }
        }
        return result;
    }

    private void initialiseCustomerDeck(String deckFile, Collection<Layer> layers, int numPlayers) {
        /*
         * 2 players: x4 Level 1, x2 Level 2, x1 Level 3
         * 3&4 players: x1 Level 1, x2 Level 2, x4 Level 3
         * 5 players: x1 Level 2, x6 Level 3
         */
        customerDeck = new Stack<>();
        List<CustomerOrder> tempCustomerDeck = new ArrayList<>();
        tempCustomerDeck.addAll(CardUtils.readCustomerFile(deckFile, layers));
        
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

        Collections.shuffle(level1CustomerOrders);
        Collections.shuffle(level2CustomerOrders);
        Collections.shuffle(level3CustomerOrders);

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
            case (3 | 4):
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
        Collections.shuffle((Stack) customerDeck);
    }

    public boolean isEmpty() {
        for(CustomerOrder customerOrder : activeCustomers) {
            if(customerOrder != null) {
                return false;
            }
        }
        return true;
    }

    public CustomerOrder peek() {
        if(isEmpty()) {
            return null;
        }
        if(activeCustomers.size() == 3) {
            return (CustomerOrder) activeCustomers.toArray()[2];
        }
        return null;
    }

    public void remove(CustomerOrder customer) {
        
        int customerIndex = ((LinkedList<CustomerOrder>) activeCustomers).indexOf(customer);
        ((LinkedList<CustomerOrder>) activeCustomers).set(customerIndex, null);
        
        //activeCustomers.remove(customer);
        inactiveCustomers.add(customer);
        customer.setStatus(CustomerOrderStatus.GIVEN_UP);
    }

    public int size() {
        int result = 0;
        for(CustomerOrder customerOrder : activeCustomers) {
            if(customerOrder != null) {
                result++;
            }
        }
        return result;
    }

    public CustomerOrder timePasses() {
        LinkedList<CustomerOrder> activeCustomerDeck = new LinkedList<>(activeCustomers);

        // Check if the customerDeck is empty
        if (customerDeck.isEmpty()) {
            // Move the leftmost non-null CustomerOrder one space to the right
            if (!activeCustomerDeck.isEmpty()) {
                CustomerOrder leftmostCustomer = null;
                int leftmostIndex = -1;

                // Find the leftmost non-null customer
                for (int i = 0; i < 3; i++) {
                    if (activeCustomerDeck.get(i) != null) {
                        leftmostCustomer = activeCustomerDeck.get(i);
                        leftmostIndex = i;
                        break;
                    }
                }

                // If a non-null customer was found, move it one space to the right
                if (leftmostCustomer != null) {
                    activeCustomerDeck.set(leftmostIndex, null);
                    activeCustomerDeck.add(leftmostIndex + 1, leftmostCustomer);
                }
    }
        } else {
            // Start from the rightmost position and shift every card to the right until you find an empty space
            for (int i = 0; i < activeCustomerDeck.size() - 1; i++) {
                // If the next space is empty, move the current card into it
                if (activeCustomerDeck.get(i + 1) == null) {
                    activeCustomerDeck.set(i + 1, activeCustomerDeck.get(i));
                    activeCustomerDeck.set(i, null);
                    break;
                }
            }
        }
        CustomerOrder leavingCustomer = null;
        // If the rightmost Customer is more than 3 spaces away from the Customer stack, discard that card
        if (activeCustomerDeck.size() > 2) {
            leavingCustomer = activeCustomerDeck.removeLast();
        }

        activeCustomers = activeCustomerDeck;
        return leavingCustomer;
    }

}
