/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.aggregator;

import java.util.ArrayList;
import java.util.Date;

/**
 *
 * @author Buhrkall
 */
public class Aggregate {

    String ssn;
    ArrayList<Message> messages;
    Date birth;

    public Aggregate(String ssn) {
        this.ssn = ssn;
        this.birth = new Date();
        this.messages = new ArrayList<Message>();

    }

    public void addMessage(Message message) {
        this.messages.add(message);
    }

    public String getSsn() {
        return ssn;
    }

    public Date getBirth() {
        return birth;
    }

    public boolean checkTime() {
        Date date = new Date();
        long timeGone = (date.getTime()- getBirth().getTime()) / 1000;
        if (timeGone >= 10) {
            return true;
        } else {
            return false;
        }
    }

    public Message getBest() {

        Message bestQoute = null;
        try {
            bestQoute = this.messages.get(0);
            for (int i = 1; i < this.messages.size(); i++) {
                if (this.messages.get(i).interestRate < bestQoute.interestRate) {
                    bestQoute = this.messages.get(i);
                }
            }
        } catch (Exception exception) {

        }

        return bestQoute;
    }

}
