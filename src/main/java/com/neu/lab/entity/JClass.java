package com.neu.lab.entity;

public final class JClass {
    /** For the names:
     * https://stackoverflow.com/questions/15202997
     *
     * (here only accepts full name, i.e., typeName)
     */
    private String name;

    public JClass(String name) {
        this.name = name;
    }

    public String getName(){
        return name;
    }


    @Override
    public int hashCode(){
        return name.hashCode();
    }

    @Override
    public boolean equals(Object object){
        if(object instanceof String)
            return name.equals(object);
        else if(object instanceof JClass)
            return name.equals(((JClass)object).getName());
        return false;
    }

    @Override
    public String toString(){
        return name;
    }
}
