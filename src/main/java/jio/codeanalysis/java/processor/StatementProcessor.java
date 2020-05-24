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
        String initializers = "";
        if (Objects.nonNull(node.initializers())) {
            @SuppressWarnings("unchecked")
            List<Object> nodes = node.initializers();
            initializers = nodes.stream().map(Object::toString).collect(Collectors.joining(","));
        }

        String expression = "";
        if (Objects.nonNull(node.getExpression())) {
            expression = node.getExpression().toString();
        }

        String updaters = "";
        if (Objects.nonNull(node.updaters())) {
            @SuppressWarnings("unchecked")
            List<Object> nodes = node.updaters();
            updaters = nodes.stream().map(Object::toString).collect(Collectors.joining(","));
        }

        String condition = String.format("for(%s; %s; %s)", initializers, expression, updaters);
        addFragmentStatement(node, StatementType.FOR_STATEMENT, condition, node.getBody());

        return false;
    }

    @Override
    public boolean visit(EnhancedForStatement node) {
        String parameter = "";
        if (Objects.nonNull(node.getParameter())) {
            parameter = node.getParameter().toString();
        }

        String expression = "";
        if (Objects.nonNull(node.getExpression())) {
            expression = node.getExpression().toString();
        }

        String condition = String.format("for(%s : %s)", parameter, expression);
        addFragmentStatement(node, StatementType.ENHANCED_FOR_STATEMENT, condition, node.getBody());
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
        if (node.getExpression() != null) {
            logger.info(String.format("  If statement expression : %s", node.getExpression().toString()));
            node.getExpression().accept(this);
        }

        if (node.getThenStatement() != null) { //if body
            logger.info(String.format("  Then statement expression : %s", node.getThenStatement().toString()));
            node.getThenStatement().accept(this);
        }

        if (node.getElseStatement() != null) { //else ~
            logger.info(String.format("  Else statement expression : %s", node.getElseStatement().toString()));
            node.getElseStatement().accept(this);
        }

        return super.visit(node);
    }

    @Override
    public boolean visit(SwitchStatement node) {
        Expression expression = node.getExpression();

        if (Objects.nonNull(expression)) {
            logger.info(String.format("  Switch statement expression : %s", expression.toString()));
        }

        if(Objects.nonNull(node.statements())) {
            for(Object s : node.statements()) {
                Statement stmtNode = (Statement) s;
                if (stmtNode instanceof SwitchCase) {
                    SwitchCase caseNode = (SwitchCase) stmtNode;

                    if (caseNode.getExpression() != null) {
                        logger.info(String.format("  Switch statement case expression : %s", caseNode.getExpression().toString()));
                        caseNode.getExpression().accept(this);
                    }
                } else {
                    stmtNode.accept(this);
                }
            }
        }

        return super.visit(node);
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
        String condition = "";
        if (Objects.nonNull(expression)) {
            condition = expression.toString();
        }

        return addFragmentStatement(node, StatementType.DO_STATEMENT, condition, body);
    }
}
