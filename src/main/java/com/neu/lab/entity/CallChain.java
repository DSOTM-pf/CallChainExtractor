package com.neu.lab.entity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 17921 on 2020/10/19
 */
public class CallChain {

    private JMethod api;
    private List<JMethod> chain;
    private int chainSize;

    public JMethod getApi() {
        return api;
    }


    public List<JMethod> getChain() {
        return chain;
    }

    public int getChainSize() {
        return chainSize;
    }


    public CallChain(List<JMethod> chain) {
        this.api = chain.get(chain.size()-1);   // api is the last one in chain
        this.chain = new ArrayList<>(chain);
        this.chainSize = chain.size();
    }
    public boolean equals(CallChain callChain)
    {
        if(!callChain.chain.get(callChain.chain.size()-1).equals(this.api))
        {
            return false;
        }
        if(callChain.chainSize!=this.chainSize) return false;
        for(int i = 0;i<this.chainSize;i++)
        {
            if(!callChain.chain.get(i).equals(this.chain.get(i))) return false;
        }
        return true;
    }
}
