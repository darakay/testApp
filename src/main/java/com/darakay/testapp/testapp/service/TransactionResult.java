package com.darakay.testapp.testapp.service;

import com.darakay.testapp.testapp.entity.Transaction;

public class TransactionResult {
    public static TransactionResult sourceOrTargetDoNotExist() {
        return null;
    }

    public static TransactionResult invalidSum() {
        return null;
    }

    public static TransactionResult ok(Transaction savedTransaction) {
        return null;
    }
}
