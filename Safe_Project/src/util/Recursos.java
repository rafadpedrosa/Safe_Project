package util;





import java.util.ArrayList;

import AST.ASTNode;
import AST.Access;
import AST.AssignSimpleExpr;
import AST.BasicCatch;
import AST.Block;
import AST.BooleanLiteral;
import AST.CatchClause;
import AST.ClassDecl;
import AST.ClassInstanceExpr;
import AST.Dot;
import AST.Expr;
import AST.ExprStmt;
import AST.FieldDeclaration;
import AST.IfStmt;
import AST.ImportDecl;
import AST.List;
import AST.MethodAccess;
import AST.MethodDecl;
import AST.Modifier;
import AST.Modifiers;
import AST.Opt;
import AST.ParameterDeclaration;
import AST.ParseName;
import AST.PrimitiveTypeAccess;
import AST.SafeStmt;
import AST.SingleTypeImportDecl;
import AST.Stmt;
import AST.StringLiteral;
import AST.TryStmt;
import AST.TypeAccess;
import AST.VarAccess;
import AST.VariableDeclaration;

public class Recursos {

	//Usado em MethodDecl
	private static int contVar = 0;
	private static List<CatchClause> clauses = new List<CatchClause>();
	private static List<Expr> createEmptyList(){return new List<Expr>();}
	private static ArrayList<SafeStmt> safes = new ArrayList<SafeStmt>();
	private static ArrayList<MethodDecl> runs = new ArrayList<MethodDecl>();

	//Criando o if(SafeManager.getInstance().isSafe(Thread thread)){...} 
	//para mudar o fluxo da thread caso ele não esteja num bloco safe.

	/**
	 * try to join a new thread with the main thread
	 * 
	 * @param the name of the param.
	 * @return a TryStmt with the join of the thread in the param.
	 */
	public static TryStmt insertJoin(String param) {

		return new TryStmt(createBlockJoin(param),createCatchSafeStmt(),
				new Opt<Block>());
	}
	/**
	 * Print all classes in the console.
	 * @param args
	 */
	public static void printClasses(ClassDecl args){
		System.out.println(args);
	}

	public static ExprStmt getSafeNodeAddThread(String variavel){
		List<Expr> list = new List<Expr>();
		list.add(new VarAccess(variavel));
		return new ExprStmt(new Dot(new VarAccess("safeNode"),
				new MethodAccess("addThread",list)));
	}


	/**
	 * Create the following structure, reserved for changes of flow.
	 * 
	 * <pre> {@code if(SafeManager.getInstance().isSafe(Thread thread)){...}}</pre>
	 * 
	 * @param the old code block.
	 * @return the new code block with the ifStatement.
	 */
	private static Block createIfStmtMethodDecl(Block blocoAntigo){

		MethodAccess getInstance = 
				new MethodAccess("getInstance",createEmptyList());

		Dot getCurrentThread = new Dot(new TypeAccess("Thread"),
				new MethodAccess("currentThread",createEmptyList()));
		MethodAccess isSafe = new MethodAccess("isSafe", 
				new List<Expr>().add(getCurrentThread));

		Dot dot2 = new Dot(getInstance,isSafe);
		Dot dot1 = new Dot(new TypeAccess("SafeManager"),dot2);
		IfStmt ifStmt = new IfStmt(dot1,addTryCatchMethodDecl(blocoAntigo),
				createElse(blocoAntigo));
		return (new Block(new List<Stmt>().add(ifStmt)));
	}

	/**
	 * Internal method to create add a "else" statement before a block
	 * 
	 * <pre>{@code else{ oldBlock };}
	 * 
	 * @param oldBlock
	 * @return the old block code with a else before it.
	 */
	private static Opt<Stmt> createElse(Block oldBlock){
		Opt<Stmt> opt = new Opt<Stmt>();
		opt.addChild(oldBlock);
		return opt;
	}

	/**
	 * Create the following sentence
	 * 
	 * <pre>{@code SafeNode safeNode = new SafeNode();}
	 * 
	 * @return the declaration of a new safeNode.
	 */
	public static VariableDeclaration newSafeNode(){

		Opt<Expr> opt = new Opt<Expr>();
		opt.addChild(new ClassInstanceExpr(
				new TypeAccess("SafeNode"),new List<Expr>()));
		return new VariableDeclaration(
				new Modifiers(), new TypeAccess("SafeNode")
				, "safeNode", opt);
	}

	/**
	 * Create the following statement:
	 * 
	 * <pre>{@code SafeManager.getInstance().addSafe(safeNode,
	 * 	SafeManager.getInstance().getSafe(
	 * 		SafeManager.getInstance().getRoot(),
	 * 		Thread.currentThread()));} </pre>
	 * 
	 * @return the code above
	 */
	public static ExprStmt addSafe(){

		TypeAccess safeManagerAccess = new TypeAccess("SafeManager");
		MethodAccess getInstanceAccess = 
				new MethodAccess("getInstance",createEmptyList());

		Dot getSafe = getSafe();

		List<Expr> parametersAddSafe = new List<Expr>();
		parametersAddSafe.add(new VarAccess("safeNode"));
		parametersAddSafe.add(getSafe);

		return new ExprStmt(new Dot(safeManagerAccess,new Dot(
				getInstanceAccess,new MethodAccess(
						"addSafe",parametersAddSafe))));
	}

	/**
	 * Create the following statement:
	 * 
	 * <pre>{@code safeNode.syncUp();}</pre>
	 * 
	 * @return the code above.
	 */
	public static ExprStmt createSyncUp(){
		return new ExprStmt(
				new Dot(new VarAccess("safeNode"),new MethodAccess(
						"syncUp", createEmptyList())));
	}
	
	/**
	 * Create the following statement:
	 * 
	 * <pre>{@code safeNode.addException(new Exception());}</pre>
	 * 
	 * @return the code above.
	 */
	public static ExprStmt addExcep(){
		return new ExprStmt(
				new Dot(new VarAccess("safeNode"),new MethodAccess(
						"addException", new List<Expr>().add(
								new ClassInstanceExpr(
										new TypeAccess("Exception"),
										createEmptyList())))));
	}
	
	/*	if(SafeManager.getInstance().isSafe(Thread.currentThread())){
			SafeManager.getInstance().getSafe(
				SafeManager.getInstance()
					.getRoot(),Thread.currentThread()).addThread(null);
		}*/

	/**
	 * Create the following statement:
	 * 
	 * <pre>{@code SafeManager.getInstance().getSafe(
	 * 	SafeManager.getInstance().getRoot(),
	 * 	Thread.currentThread()).addThread(thread);}</pre>
	 * 
	 * @return the code above.
	 */
	private static IfStmt createIfSafeAddThread(String thread){

		Dot internalDot = new Dot(new TypeAccess("Thread"),new MethodAccess(
				"currentThread",createEmptyList()));
		Dot midDot = new Dot(new MethodAccess("getInstance",createEmptyList()),
				new MethodAccess("isSafe", new List<Expr>().add(internalDot)));
		Dot externalDot = new Dot(new TypeAccess("SafeManager"),midDot);

		
		Dot getSafe = getSafe();
		Dot addThread = new Dot(getSafe,new MethodAccess("addThread",
				new List<Expr>().add(new VarAccess(thread))));
		
		ExprStmt exprStmt = new ExprStmt(addThread);
		
		return new IfStmt(externalDot, exprStmt);
	}

	/**
	 * Internal method to create the following line: 
	 * (this line is used in several places)
	 * 
	 * <pre>{@code SafeManager.getInstance().getSafe(
	 * 	SafeManager.getInstance().getRoot(),
	 * 	Thread.currentThread());}</pre>
	 * 
	 * @return the code above.
	 */
	private static Dot getSafe() {
		TypeAccess safeManagerAccess = new TypeAccess("SafeManager");
		MethodAccess getInstanceAccess = 
				new MethodAccess("getInstance",createEmptyList());

		List<Expr> parametersGetSafe = new List<Expr>();

		parametersGetSafe.add(
				new Dot(safeManagerAccess,new Dot(getInstanceAccess
						,new MethodAccess("getRoot",createEmptyList()))));
		parametersGetSafe.add(
				new Dot(new TypeAccess("Thread"),new MethodAccess(
						"currentThread",createEmptyList())));

		return new Dot(safeManagerAccess,new Dot(getInstanceAccess,
				new MethodAccess("getSafe", parametersGetSafe)));
	}

	/** Internal Method that gets the old block and transforme it, 
	 * adding a try/catch:
	 *<pre> {@code try{"oldBlock with safe modifications"}
  catch("Exceptions"){(...)}
  finally{
   SafeManager.getInstance().getSafe(
	 SafeManager.getInstance().getRoot(),
	 Thread.currentThread()).tryWakeUp(
	 	Thread.currentThread());
  }</pre>
	 * @param blocoAntigo
	 * @return
	 */

	private static Block addTryCatchMethodDecl(Block blocoAntigo) {
		Opt<Block> opt = new Opt<Block>();
		Block block = new Block();
		block.addStmt(tryWakeUp());
		opt.addChild(block);
		TryStmt tryStmt = new TryStmt(modifedOldBlock(blocoAntigo),
				createCatchMethodDecl(),opt);
		return (new Block(new List<Stmt>().add(tryStmt)));
	}

	/**
	 * 
	 * @param oldBlock with the original informations
	 * @return newBlock with the safe included
	 */
	private static Block modifedOldBlock(Block oldBlock){

		//Create a new Block with all the old information
		List<Stmt> lista = new List<Stmt>();
		for (int i = 0; i < oldBlock.getChild(0).getNumChild(); i++) {
			lista.add((Stmt) oldBlock.getChild(0).getChild(i));
		}
		Block newBlock = new Block(lista);

		for(int i = 0; i < oldBlock.getNumStmt(); i++) {

			Stmt stmtAtual = oldBlock.getStmt(i);
			boolean ehClassInstance = false;
			String args = stmtAtual.toString();
			Class<? extends Stmt> classe = stmtAtual.getClass();
			if(classe.equals(ExprStmt.class)){
				if(stmtAtual.getChild(0).getClass().equals(Dot.class)){
					//In the case there's a new Instance of some class, 
					//I will check if it's new Thread
					if(stmtAtual.getChild(0).getChild(0).getClass().equals(
							ClassInstanceExpr.class))
						ehClassInstance = true;
				}
			}

			//TODO
			//isso precisa ser urgentemente melhorado
			if(args.contains((CharSequence) "start")){
				System.out.println("opa meu vei");	
				//				newBlock.addStmt(Recursos.createIfSafeAddThread(
				//						((Dot) ((ExprStmt) stmtAtual).getExpr()).getLeft().toString()),i+1);
				if((ehClassInstance)){

				}else if(!args.contains((CharSequence) "variavelNova")){
				}
			}
		}

		return newBlock;
	}

	/**
	 * Create the following statement:
	 * 
	 * <pre>{@code 	SafeManager.getInstance().getSafe(
	 * 	SafeManager.getInstance().getRoot(),
	 * 	Thread.currentThread()).tryWakeUp(
	 * 		Thread.currentThread());}</pre>
	 * 
	 * @return the code above.
	 */	
	public static ExprStmt tryWakeUp(){
		return new ExprStmt(new Dot(getSafe(),new MethodAccess("tryWakeUp",
				new List<Expr>().add(new Dot(new TypeAccess("Thread"),
						new MethodAccess("currentThread",createEmptyList()))))));
	}

	//SafeManager.getInstance().getSafe(SafeManager.getInstance().getRoot(),Thread.currentThread()).breakUp();

	/**
	 * Create the following statement:
	 * 
	 * <pre>{@code 	SafeManager.getInstance().getSafe(
	 * 	SafeManager.getInstance().getRoot(),
	 * 	Thread.currentThread()).breakUp();}</pre>
	 * 
	 * @return the code above.
	 */
	public static ExprStmt createBreakUp(){
		return new ExprStmt(new Dot(getSafe(),
				new MethodAccess("breakUp",createEmptyList())));
	}

	/**
	 * Internal Method to create the Catch for the exceptions for the safe
	 * 
	 * @return catch for all the exceptions
	 */
	private static List<CatchClause> createCatchMethodDecl(){
		List<CatchClause> listaCatch = new List<CatchClause>();

		//TODO
		//isso é um Stub
		//		for (int i = 0; i < clauses.getNumChild(); i++) {
		//			listaCatch.add(clauses.getChild(i));
		//		}
		Modifiers mods = new Modifiers(new List<Modifier>());
		TypeAccess typeAcess = new TypeAccess("Exception");
		ParameterDeclaration param = 
				new ParameterDeclaration(mods, typeAcess, "e");

		ExprStmt args = new ExprStmt(StmtSyso());

		Block bloco = new Block();
		bloco.addStmt(createBreakUp());
		bloco.addStmt(args);

		BasicCatch catchs = new BasicCatch(param,bloco);
		listaCatch.add(catchs);
		return listaCatch;
	}

//	public static ExprStmt finalTest(){
//		Dot dot = new Dot(new TypeAccess("Thread"),new MethodAccess("currentThread",new List<Expr>()));
//		List<Expr> listExpr = new List<Expr>();
//		listExpr.add(dot);
//		MethodAccess acess = new MethodAccess("existsThread",listExpr);
//
//		List<Expr> lista = new List<Expr>();
//
//		MethodAccess methodAcess = new MethodAccess("println", lista);
//		VarAccess varAcess = new VarAccess("out");
//
//		lista.add(new Dot(new VarAccess("safeNode"),acess));
//		Dot dot2 = new Dot();
//		dot2.setRight(methodAcess);
//		dot2.setLeft(varAcess);
//
//		return new ExprStmt(new Dot(new TypeAccess("System"),dot2));
//	}

	/**
	 * Stub para verificar se a thread foi interrompida corretamente.
	 * Insere a seguinte linha no tratamento da exceção:
	 * <pre> {@code System.out.println("Thread Interrompida");} </pre>
	 * 
	 * @return linha de código acima, no formato de uma Expr
	 */
	private static Expr StmtSyso() {
		List<Expr> lista = new List<Expr>();
		StringLiteral string = new StringLiteral("Thread Interrompida");
		MethodAccess methodAcess = new MethodAccess("println", lista);
		VarAccess varAcess = new VarAccess("out");

		lista.add(string);
		Dot dot2 = new Dot();
		dot2.setRight(methodAcess);
		dot2.setLeft(varAcess);

		return new Dot(new TypeAccess("System"),dot2);
	}


	/**
	 * Internal method that creates a block with the following code:
	 * 
	 * <pre>{@code param.join}</pre>
	 * 
	 * @param the name of the param.
	 * @return a block with the code above.
	 */
	private static Block createBlockJoin(String param) {
		MethodAccess methodAccess = new MethodAccess("join",new List<Expr>());
		VarAccess access = new VarAccess(param);
		Dot dot = new Dot(access,methodAccess);
		ExprStmt expr = new ExprStmt(dot);
		List<Stmt> listBlock = new List<Stmt>();
		listBlock.add(expr);
		return new Block(listBlock);
	}

	/**
	 * Internal method that creates a catch block to treat the InterruptedException of a join.
	 * 
	 * <pre>{@code catch(InterruptedException e){
	 * 	System.out.println("Thread Interrompida");
	 * }
	 * </pre>
	 * 
	 * @return the code above:
	 */
	private static List<CatchClause> createCatchSafeStmt(){
		List<CatchClause> listaCatch = new List<CatchClause>();
		Modifiers mods = new Modifiers(new List<Modifier>());
		TypeAccess typeAcess = new TypeAccess("InterruptedException");
		ParameterDeclaration param = 
				new ParameterDeclaration(mods, typeAcess, "e");

		List<Stmt> listaBlock = new List<Stmt>();
		ExprStmt args = new ExprStmt(StmtSyso());
		listaBlock.add(args);
		Block bloco = new Block(listaBlock);

		BasicCatch catchs = new BasicCatch(param,bloco);
		listaCatch.add(catchs);
		return listaCatch;

	}
	/**
	 * starts a new thread.
	 * 
	 * @param the name of the param.
	 * @return a TryStmt with the join of the thread in the param.
	 */
	public static ExprStmt insertStart(String param) {
		VarAccess access = new VarAccess(param);
		MethodAccess methodAccess = new MethodAccess("start",new List<Expr>());
		Dot dot = new Dot(access,methodAccess);
		return new ExprStmt(dot);
	}

	/**
	 * 
	 * @param stmt
	 * @param param
	 * @return
	 */
	public static VariableDeclaration insertDecl(ExprStmt stmt,String param) {
		Modifiers mods = new Modifiers(new List<Modifier>());
		ClassInstanceExpr classInst = (ClassInstanceExpr) stmt.getChild(0).getChild(0);
		Opt opt = new Opt (classInst);
		Access acesso = (Access) stmt.getChild(0).getChild(0).getChild(0);
		VariableDeclaration declar = new VariableDeclaration(mods, acesso, param,opt);

		return declar;
	}

	//import java.util.ArrayList;
	//import java.util.concurrent.atomic.AtomicInteger;
	//import javax.swing.tree.DefaultMutableTreeNode;
	//import javax.swing.tree.DefaultTreeModel;
	//import javax.swing.tree.MutableTreeNode;

	/**
	 * Add the following imports, once they are necessary 
	 * for the execution of the safe.
	 * 
	 * <pre> {@code import java.util.ArrayList;
	 * import java.util.concurrent.atomic.AtomicInteger;
	 * import javax.swing.tree.DefaultMutableTreeNode;
	 * import javax.swing.tree.DefaultTreeModel;
	 * import javax.swing.tree.MutableTreeNode;}</pre>
	 * 
	 * @param the old list of imports.
	 * @return the old list with the new imports in it.
	 */

	public static List<ImportDecl> createImports(List<ImportDecl> p1) {

		SingleTypeImportDecl importArrayList = 
				new SingleTypeImportDecl(
						new Dot(new ParseName("java"),
								new Dot(new ParseName("util"),
										new ParseName("ArrayList"))));
		SingleTypeImportDecl importAtomicInteger = 
				new SingleTypeImportDecl(
						new Dot(new ParseName("java"),
								new Dot(new ParseName("util"),
										new Dot(new ParseName("concurrent"),
												new Dot(new ParseName("atomic"),
														new ParseName("AtomicInteger"))))));
		SingleTypeImportDecl importDefaultMutableTreeNode = 
				new SingleTypeImportDecl(
						new Dot(new ParseName("javax"),
								new Dot(new ParseName("swing"),
										new Dot(new ParseName("tree"),
												new ParseName("DefaultMutableTreeNode")))));
		SingleTypeImportDecl importDefaultTreeModel = 
				new SingleTypeImportDecl(
						new Dot(new ParseName("javax"),
								new Dot(new ParseName("swing"),
										new Dot(new ParseName("tree"),
												new ParseName("DefaultTreeModel")))));
		SingleTypeImportDecl importMutableTreeNode = 
				new SingleTypeImportDecl(
						new Dot(new ParseName("javax"),
								new Dot(new ParseName("swing"),
										new Dot(new ParseName("tree"),
												new ParseName("MutableTreeNode")))));

		p1.add(importArrayList);
		p1.add(importMutableTreeNode);
		p1.add(importDefaultMutableTreeNode);
		p1.add(importAtomicInteger);
		p1.add(importDefaultTreeModel);

		return p1;
	}
	public static void setCatchs(List<CatchClause> catchClauseList) {
		clauses = catchClauseList;

	}
	public static Block updateRun(MethodDecl methodRun, Block block) {
		if(methodRun.getID().equals("run") && !runs.contains(methodRun)){
				runs.add(methodRun);
				return createIfStmtMethodDecl(block);
		}
		return block;
	}

	/** Create the following statement:

	  <pre>{@code static boolean isSafe}</pre>

	  @return the code above.
	 */
	//public static FieldDeclaration getField() {
	//	List<Modifier> lista = new List<Modifier>();
	//	lista.add(new Modifier("static"));
	//	Modifiers mods = new Modifiers(lista);
	//	PrimitiveTypeAccess boolAcess = new PrimitiveTypeAccess("boolean");
	//	return new FieldDeclaration(mods,boolAcess,"isSafe");
	//}

	/**
	 * Used to test the flow of execution.
	 * 
	 * @param args
	 */
	//public static void recursividade(ASTNode args){
	//
	//	System.out.println("filhos de " + args.getClass());
	//
	//	//		if (args.getClass() == SingleTypeImportDecl.class) {
	//	//			System.out.println("AOPAOPAOPAOAPAOPAOP");
	//	//			System.out.println(args);
	//	//		}
	//
	//	for (int j = 0; j < args.getNumChild(); j++) {
	//		ASTNode temp = args.getChild(j);
	//		System.out.println("	#"+j+" = " +temp.getClass());
	//	}
	//
	//	for (int j = 0; j < args.getNumChild(); j++) {
	//		ASTNode temp = args.getChild(j);
	//		recursividade(temp);
	//	}
	//	System.out.println("FIM DOS FILHOS DE " + args.getClass());
	//}

	/**
	 * sets the atribute "isSafe" to false using the following code:
	 * 
	 * <pre>{@code isSafe = false;} </pre>
	 * 
	 * @param stmt
	 * @return 
	 * @return the code above.
	 */
	//public static ExprStmt setSafe(ExprStmt stmt,boolean bool) {
	//	ClassInstanceExpr classInst = 
	//			(ClassInstanceExpr) stmt.getChild(0).getChild(0);
	//	ClassInstanceExpr runzinho = 
	//			(ClassInstanceExpr) classInst.getArgList().getChild(0);
	//	Dot dot = new Dot();
	//	TypeAccess typeAcess = new TypeAccess(runzinho.getAccess().toString());
	//	VarAccess varAcess = new VarAccess("isSafe");
	//	dot.setLeft(typeAcess);
	//	dot.setRight(varAcess);
	//	BooleanLiteral boole = new BooleanLiteral(bool);
	//	AssignSimpleExpr assSimExpr = new AssignSimpleExpr(dot, boole);
	//	return new ExprStmt(assSimExpr);
	//}


	public static void updateSafeStmt(Block block){
		for (int i = 0; i < block.getStmtList().getNumChild(); i++) {
			if(block.getStmt(i).getClass().equals(SafeStmt.class)){
				SafeStmt safeStmt = (SafeStmt) block.getStmt(i);
				if(!safes.contains(safeStmt)){
					safes.add(safeStmt);
					block.addStmt(newSafeNode(),0);
					block.addStmt(addSafe(),1);
					initSafe(safeStmt);
				}
			}
		}
	}
	private static void initSafe(SafeStmt safeStmt) {
		//TODO
		//FIXME
		System.out.println("opa");
		for (int i = 0; i < safeStmt.getBlock().getNumStmt(); i++) {

			Stmt stmtAtual = safeStmt.getBlock().getStmt(i);
			boolean ehClassInstance = false;
			String args = stmtAtual.toString();
			Class<? extends Stmt> classe = stmtAtual.getClass();
			if (classe.equals(ExprStmt.class)) {
				if (stmtAtual.getChild(0).getClass().equals(Dot.class)) {
					if (stmtAtual.getChild(0).getChild(0).getClass()
							.equals(ClassInstanceExpr.class))
						ehClassInstance = true;
				}
			}
			//TODO
			//isso precisa ser urgentemente melhorado
			if (args.contains((CharSequence) "start")) {
				if ((ehClassInstance)) {
					ExprStmt stmt = (ExprStmt) stmtAtual;
					String param = "variavelNova" + contVar;
					contVar++;
					//getBlock().setStmt(util.Recursos.setSafe(stmt),i);
					addLines(safeStmt, i, stmt, param);
				} else if (!args
						.contains((CharSequence) "variavelNova")) {
					String variableName = ((VarAccess) ((Dot) stmtAtual
							.getChild(0)).getLeft()).getID();
					safeStmt.getBlock().addStmt(
							util.Recursos.getSafeNodeAddThread(variableName), i + 1);
					safeStmt.getBlock().addStmt(
							util.Recursos.insertJoin(variableName),
							safeStmt.getBlock().getNumStmt());
				}
			}

		}

	}
	private static void addLines(
			SafeStmt safeStmt, int i, ExprStmt stmt,String param) {
		safeStmt.getBlock().setStmt(util.Recursos.insertDecl(stmt, param),i);
		safeStmt.getBlock().addStmt(
				util.Recursos.getSafeNodeAddThread(param),i+1);
		safeStmt.getBlock().addStmt(
				util.Recursos.insertStart(param),i+2);
		safeStmt.getBlock().addStmt(
				util.Recursos.insertJoin(param),
				safeStmt.getBlock().getNumStmt());
	}
	public static IfStmt checkThread(Stmt stmt, Block block) {
		if(stmt.getClass().equals(VariableDeclaration.class)){
			VariableDeclaration decl = (VariableDeclaration) stmt;
			if(decl.getTypeAccess().toString().equals("Thread")){
				return Recursos.createIfSafeAddThread(decl.getID());
			}
		}
		return null;
	}

}
