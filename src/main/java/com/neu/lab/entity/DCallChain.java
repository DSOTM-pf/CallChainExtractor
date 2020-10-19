package com.neu.lab.entity;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Stands for Dangerous Call Chain
 */
public class DCallChain {

    private JMethod api;
//    private Set<APermission> permissions;
    private List<JMethod> chain;
    private int chainSize;

    public JMethod getApi() {
        return api;
    }

//    public Set<APermission> getPermissions() {
//        return permissions;
//    }

    public List<JMethod> getChain() {
        return chain;
    }

    public int getChainSize() {
        return chainSize;
    }


    public DCallChain(List<JMethod> chain) {
        this.api = chain.get(chain.size()-1);   // api is the last one in chain
        this.chain = new ArrayList<>(chain);
        this.chainSize = chain.size();
    }
/*
    public DCallChain(List<JMethod> chain, Set<APermission> permissions) {
        this.api = chain.get(chain.size()-1);   // api is the last one in chain
        this.permissions = new LinkedHashSet<>(permissions);
        this.chain = new ArrayList<>(chain);
        this.chainSize = chain.size();
    }*/

}
