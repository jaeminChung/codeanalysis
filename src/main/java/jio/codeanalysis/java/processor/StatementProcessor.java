package jio.codeanalysis.java.processor;

import jio.codeanalysis.java.model.JavaMethod;
import jio.codeanalysis.java.model.JavaStatement;
import jio.codeanalysis.java.model.StatementType;
import org.eclipse.jdt.core.dom.*;
import org.jboss.logging.Logger;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;


public class StatementProcessor extends ASTVisitor {
    final static Logger logger = Logger.getLogger(StatementProcessor.class.getName());
    final private JavaMethod method;
    final private JavaStatement parentStatement;

    public StatementProcessor(JavaMethod method, JavaStatement parentStatement) {
        this.method = method;
        this.parentStatement = parentStatement;
    }

    @Override
    public boolean visit(VariableDeclarationStatement node) {
        addStatement(node, StatementType.VARIABLE_DECLARATION_STATEMENT);

        return super.visit(node);
    }

    @Override
    public boolean visit(AssertStatement node) {
        addStatement(node, StatementType.ASSERT_STATEMENT);

        return false;
    }

    @Override
    public boolean visit(BreakStatement node) {
        addStatement(node, StatementType.BREAK_STATEMENT);

        return super.visit(node);
    }

    @Override
    public boolean visit(ExpressionStatement node) {
        addStatement(node, StatementType.EXPRESSION_STATEMENT);

        return super.visit(node);
    }

    @Override
    public boolean visit(ConstructorInvocation node) {
        addStatement(node, StatementType.CONSTRUCTOR_INVOCATION);

        return super.visit(node);
    }

    @Override
    public boolean visit(SuperConstructorInvocation node) {
        addStatement(node, StatementType.SUPER_CONSTRUCTOR_INVOCATION);

        return super.visit(node);
    }

    @Override
    public boolean visit(ContinueStatement node) {
        addStatement(node, StatementType.CONTINUE_STATEMENT);

        return super.visit(node);
    }

    @Override
    public boolean visit(EmptyStatement node) {
        addStatement(node, StatementType.EMPTY_STATEMENT);

        return super.visit(node);
    }

    @Override
    public boolean visit(LabeledStatement node) {
        addStatement(node, StatementType.LABELED_STATEMENT);

        return super.visit(node);
    }

    @Override
    public boolean visit(ReturnStatement node) {
        addStatement(node, StatementType.RETURN_STATEMENT);

        return super.visit(node);
    }

    @Override
    public boolean visit(TypeDeclarationStatement node) {
        addStatement(node, StatementType.TYPE_DECLARATION_STATEMENT);

        return super.visit(node);
    }

    @Override
    public boolean visit(ThrowStatement node) {
        addStatement(node, StatementType.THROW_STATEMENT);

        return super.visit(node);
    }

    @Override
    public boolean visit(ForStatement node) {
        JavaStatement statement = addFragmentStatement(node, StatementType.FOR_STATEMENT, "", node.getBody());

        String initializers = "";
        if (Objects.nonNull(node.initializers())) {
            @SuppressWarnings("unchecked")
            List<Expression> nodes = node.initializers();
            initializers = nodes.stream().map(Object::toString).collect(Collectors.joining(","));
            nodes.forEach(each -> each.accept(new MethodInvocationProcessor(statement)));
        }

        String expression = "";
        if (Objects.nonNull(node.getExpression())) {
            expression = node.getExpression().toString();
            node.getExpression().accept(new MethodInvocationProcessor(statement));
        }

        String updaters = "";
        if (Objects.nonNull(node.updaters())) {
            @SuppressWarnings("unchecked")
            List<Expression> nodes = node.updaters();
            updaters = nodes.stream().map(Object::toString).collect(Collectors.joining(","));
            nodes.forEach(each -> each.accept(new MethodInvocationProcessor(statement)));
        }

        String condition = String.format("for(%s; %s; %s)", initializers, expression, updaters);
        statement.setStatement(condition);

        return false;
    }

    @Override
    public boolean visit(EnhancedForStatement node) {
        JavaStatement statement = addFragmentStatement(node, StatementType.ENHANCED_FOR_STATEMENT, "", node.getBody());

        String parameter = "";
        if (Objects.nonNull(node.getParameter())) {
            parameter = node.getParameter().toString();
        }

        String expression = "";
        if (Objects.nonNull(node.getExpression())) {
            expression = node.getExpression().toString();
            node.getExpression().accept(new MethodInvocationProcessor(statement));
        }

        String condition = String.format("for(%s : %s)", parameter, expression);
        statement.setStatement(condition);
        return false;
    }

    @Override
    public boolean visit(DoStatement node) {
        addExpressionFragmentStatement(node, node.getBody(), node.getExpression(), StatementType.DO_STATEMENT);
        return false;
    }

    @Override
    public boolean visit(SynchronizedStatement node) {
        addExpressionFragmentStatement(node, node.getBody(), node.getExpression(), StatementType.SYNCHRONIZED_STATEMENT);
        return false;
    }

    @Override
    public boolean visit(WhileStatement node) {
        addExpressionFragmentStatement(node, node.getBody(), node.getExpression(), StatementType.WHILE_STATEMENT);
        return false;
    }

    @Override
    public boolean visit(IfStatement node) {
        JavaStatement ifStatement = addExpressionFragmentStatement(node, node.getThenStatement(), node.getExpression(), StatementType.IF_STATEMENT);
        if (node.getElseStatement() != null) { //else ~
            JavaStatement elseStatement = addFragmentStatement(node.getElseStatement(), StatementType.ELSE_STATEMENT, "", node.getElseStatement());
            ifStatement.addSiblingStatement(elseStatement);
        }

        return false;
    }

    @Override
    public boolean visit(SwitchStatement node) {
        JavaStatement switchStatement = addExpressionFragmentStatement(node, null, node.getExpression(), StatementType.SWITCH_STATEMENT);

        if(Objects.nonNull(node.statements())) {
            JavaStatement switchCaseStatement = null;
            for(Object s : node.statements()) {
                Statement stmtNode = (Statement) s;
                if (stmtNode instanceof SwitchCase) {
                    SwitchCase caseNode = (SwitchCase) stmtNode;
                    switchCaseStatement = addExpressionFragmentStatement(caseNode, caseNode, caseNode.getExpression(), StatementType.SWITCH_CASE);
                    switchStatement.addSiblingStatement(switchCaseStatement);
                } else {
                    stmtNode.accept(new StatementProcessor(method, switchCaseStatement));
                }
            }
        }

        return false;
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean visit(TryStatement node) {
        List<Expression> resources = node.resources();
        String resource = resources.stream().map(Object::toString).collect(Collectors.joining("\n"));
        JavaStatement tryStatement = addFragmentStatement(node, StatementType.TRY_STATEMENT, resource, node.getBody());
        resources.forEach(each -> each.accept(new MethodInvocationProcessor(tryStatement)));

        for(Object o : node.catchClauses()) {
            CatchClause cat = (CatchClause) o;
            JavaStatement catchStatement = addFragmentStatement(cat, StatementType.CATCH_STATEMENT, cat.getException().toString(), cat.getBody());
            tryStatement.addSiblingStatement(catchStatement);
        }
        Block finallyBlock = node.getFinally();
        if(Objects.nonNull(finallyBlock)) {
            JavaStatement finallyStatement = addFragmentStatement(finallyBlock, StatementType.FINALLY_STATEMENT, "", finallyBlock);
            tryStatement.addSiblingStatement(finallyStatement);
        }
        return false;
    }

    @Override
    public boolean visit(Block node) {
        ASTNode parent = node.getParent();
        if (parent instanceof Block) {
            JavaStatement statement = new JavaStatement();

            statement.setStartPos(node.getStartPosition());
            statement.setLength(node.getLength());
            statement.setStatementType(StatementType.BLOCK_STATEMENT);
            statement.setStatement("");
            method.addStatement(statement);
            if (Objects.nonNull(parentStatement))
                parentStatement.addChildStatement(statement);

            @SuppressWarnings("unchecked")
            List<Statement> statements = (List<Statement>) node.statements();
            if (Objects.nonNull(statements)) {
                statements.forEach(s -> s.accept(new StatementProcessor(method, statement)));
            }
            return false;
        }
        return super.visit(node);
    }

    private JavaStatement addStatement(ASTNode node
            , StatementType statementType) {
        JavaStatement statement = new JavaStatement();

        statement.setStartPos(node.getStartPosition());
        statement.setLength(node.getLength());
        statement.setStatementType(statementType);
        statement.setStatement(node.toString());
        method.addStatement(statement);
        if (Objects.nonNull(parentStatement))
            parentStatement.addChildStatement(statement);

        node.accept(new MethodInvocationProcessor(statement));
        return statement;
    }

    private JavaStatement addFragmentStatement(ASTNode node, StatementType statementType, String condition, Statement body) {
        JavaStatement statement = new JavaStatement();

        statement.setStartPos(node.getStartPosition());
        statement.setLength(node.getLength());
        statement.setStatementType(statementType);
        statement.setStatement(condition);
        method.addStatement(statement);
        if (Objects.nonNull(parentStatement))
            parentStatement.addChildStatement(statement);

        if (body != null) {
            body.accept(new StatementProcessor(method, statement));
        }

        return statement;
    }

    private JavaStatement addExpressionFragmentStatement(ASTNode node, Statement body, Expression expression, StatementType statementType) {
        JavaStatement statement = addFragmentStatement(node, statementType, "", body);
        if (Objects.nonNull(expression)) {
            statement.setStatement(expression.toString());
            expression.accept(new MethodInvocationProcessor(statement));
        }

        return statement;
    }
}
