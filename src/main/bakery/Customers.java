package bakery;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Stack;
import java.util.stream.Collectors;
import java.io.Serializable;

import util.CardUtils;

import bakery.CustomerOrder.CustomerOrderStatus;

public class Customers implements Serializable {
    private Collection<CustomerOrder> activeCustomers;
    private Collection<CustomerOrder> customerDeck;
    private List<CustomerOrder> inactiveCustomers;
    private Random random;

    private final long serialVersionUID = 11085168;


    public Customers(String deckFile, Random random, Collection<Layer> layers, int numPlayers) {
        // initialise activeCustomers
        initialiseCustomerDeck(deckFile, layers, numPlayers);
        inactiveCustomers = new ArrayList<>();
    }

    public CustomerOrder addCustomerOrder() {
        inactiveCustomers.add(timePasses());
        return null;
    }

    public boolean customerWillLeaveSoon() {
        for(CustomerOrder customer : activeCustomers) {
            if(customer.getStatus().equals(CustomerOrderStatus.IMPATIENT)) {
                return true;
            }
        }
        return false;
    }

    public CustomerOrder drawCustomer() {
        return ((Stack<CustomerOrder>) customerDeck).pop();
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
            if (hand.containsAll(customerOrder.getRecipe())) {
                result.add(customerOrder);
            }
        }
        return (Collection) result;
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
        customerDeck.addAll(CardUtils.readCustomerFile(deckFile, layers));
        Collections.shuffle((List) customerDeck, random);

        List<CustomerOrder> level1CustomerOrders = customerDeck.stream()
            .filter(order -> order.getLevel() == 1)
            .collect(Collectors.toList());

        List<CustomerOrder> level2CustomerOrders = customerDeck.stream()
            .filter(order -> order.getLevel() == 2)
            .collect(Collectors.toList());

        List<CustomerOrder> level3CustomerOrders = customerDeck.stream()
            .filter(order -> order.getLevel() == 3)
            .collect(Collectors.toList());

        switch(numPlayers) {
            case 2:
                activeCustomers.add(level1CustomerOrders.get(0));
                activeCustomers.add(level1CustomerOrders.get(1));
                activeCustomers.add(level1CustomerOrders.get(2));
                activeCustomers.add(level1CustomerOrders.get(3));
                activeCustomers.add(level2CustomerOrders.get(0));
                activeCustomers.add(level2CustomerOrders.get(1));
                activeCustomers.add(level3CustomerOrders.get(0));
                break;
            case (3 | 4):
                activeCustomers.add(level1CustomerOrders.get(0));
                activeCustomers.add(level2CustomerOrders.get(0));
                activeCustomers.add(level2CustomerOrders.get(1));
                activeCustomers.add(level3CustomerOrders.get(0));
                activeCustomers.add(level3CustomerOrders.get(1));
                activeCustomers.add(level3CustomerOrders.get(2));
                activeCustomers.add(level3CustomerOrders.get(3));
                break;
            case 5:
                activeCustomers.add(level2CustomerOrders.get(0));
                activeCustomers.add(level3CustomerOrders.get(0));
                activeCustomers.add(level3CustomerOrders.get(1));
                activeCustomers.add(level3CustomerOrders.get(2));
                activeCustomers.add(level3CustomerOrders.get(3));
                activeCustomers.add(level3CustomerOrders.get(4));
                activeCustomers.add(level3CustomerOrders.get(5));
                break;
        }
        Collections.shuffle((List) customerDeck, random);
    }

    public boolean isEmpty() {
        if(activeCustomers.isEmpty()) {
            return true;
        }
        return false;
    }

    public CustomerOrder peek() {
        if(customerDeck.isEmpty()) {
            return null;
        }
        return ((Stack<CustomerOrder>) customerDeck).peek();
    }

    public void remove(CustomerOrder customer) {
        activeCustomers.remove(customer);
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
            // Get the top card and remove it from the deck
            CustomerOrder newCustomer = ((Stack<CustomerOrder>) customerDeck).pop();
            activeCustomerDeck.addFirst(newCustomer);
        }
        
        if(!activeCustomerDeck.get(3).equals(null)) {
            return activeCustomerDeck.get(3);
        }
        activeCustomers = activeCustomerDeck;
        return null;
    }
}
