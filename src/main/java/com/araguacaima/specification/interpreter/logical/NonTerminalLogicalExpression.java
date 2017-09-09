/*
 * Copyright 2017 araguacaima
 *
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.araguacaima.specification.interpreter.logical;

import com.araguacaima.specification.interpreter.Expression;
import com.araguacaima.specification.interpreter.NonTerminalExpression;

public abstract class NonTerminalLogicalExpression extends LogicalExpression implements NonTerminalExpression {
    private Expression leftNode;

    private Expression rightNode;

    public NonTerminalLogicalExpression(Expression l, Expression r) {
        this(false, l, r);
    }

    NonTerminalLogicalExpression(boolean evaluateAllTerms, Expression l, Expression r) {
        super();
        setEvaluateAllTerms(evaluateAllTerms);
        setLeftNode(l);
        setRightNode(r);
    }

    public NonTerminalLogicalExpression(boolean value) {
        super(value);
    }

    private void setLeftNode(Expression node) {
        leftNode = node;
    }

    private void setRightNode(Expression node) {
        rightNode = node;
    }

    public Expression getLeftNode() {
        return leftNode;
    }

    public Expression getRightNode() {
        return rightNode;
    }
}// NonTerminalExpression
