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

    public CustomerOrder addCustomerOrder() {
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
        return customerDeck;
    }

    public Collection<CustomerOrder> getFulfilable(List<Ingredient> hand) {
        Collection<CustomerOrder> result = new ArrayList<>();
        for (CustomerOrder customerOrder : activeCustomers) {
            if(customerOrder != null && customerOrder.canFulfill(hand)) {
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
        if(activeCustomers.size() > 1) {
            return ((LinkedList<CustomerOrder>) activeCustomers).getLast();
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
