/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.aggregator;

/**
 *
 * @author Buhrkall
 */
public class Message {
    String ssn;
    double interestRate;
    String bankName;

    public Message(String ssn, double interestRate, String bankName) {
        this.ssn = ssn;
        this.interestRate = interestRate;
        this.bankName = bankName;
    }
  }
