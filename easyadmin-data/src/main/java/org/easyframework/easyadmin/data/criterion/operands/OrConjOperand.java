package org.easyframework.easyadmin.data.criterion.operands;

public class OrConjOperand extends AbstractOperand {

	public OrConjOperand() {
	}

	@Override
	protected String toExpression() {
		return " OR ";
	}
}
