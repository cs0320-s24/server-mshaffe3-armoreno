package APIHandlers;

import java.util.Arrays;

public class Location {

    String[] loc;

    public Location(String[] location){
        this.loc = location;
    }

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

    @Override
    public int hashCode() {
        return Arrays.hashCode(loc);
    }
}
