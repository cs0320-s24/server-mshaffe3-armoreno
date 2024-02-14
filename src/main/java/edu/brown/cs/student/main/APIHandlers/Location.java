package APIHandlers;

import java.util.Arrays;

/**
 * This class overrides a location string[]'s equality for cache purposes
 */
public class Location {

    String[] loc;

    /**
     * Instantiates the location to override equality
     * @param location - the county and state in a String array
     */
    public Location(String[] location){
        this.loc = location;
    }

    /**
     * overrides equality in Location so that two separate arrays are equal if they have the exact same content
     * @param o - Location to be checked against this one
     * @return - true if the arrays are equal and false if not a Location or null or not equal
     */
    @Override
    public boolean equals(Object o){
        if(this == o){
            return true;
        }
        if (o == null || getClass() != o.getClass()) return false;
        Location that = (Location) o;
        System.out.println(Arrays.equals(loc, that.loc));
        return Arrays.equals(this.loc, that.loc);
    }

    /**
     * overrides loc's hashcode so that codes with the same content are regarded as the same
     * @return a hashcode based on the contents of the array
     */
    @Override
    public int hashCode() {
        return Arrays.hashCode(loc);
    }
}
