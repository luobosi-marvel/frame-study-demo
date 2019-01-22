package com.marvel.fsd;

import com.greenpineyu.fel.Expression;
import com.greenpineyu.fel.FelEngine;
import com.greenpineyu.fel.FelEngineImpl;
import com.greenpineyu.fel.common.FelBuilder;
import com.greenpineyu.fel.common.ObjectUtils;
import com.greenpineyu.fel.context.AbstractContext;
import com.greenpineyu.fel.context.ContextChain;
import com.greenpineyu.fel.context.FelContext;
import com.greenpineyu.fel.context.MapContext;
import com.greenpineyu.fel.function.CommonFunction;
import com.greenpineyu.fel.function.Function;
import com.greenpineyu.fel.interpreter.ConstInterpreter;
import com.greenpineyu.fel.interpreter.Interpreter;
import com.greenpineyu.fel.optimizer.Interpreters;
import com.greenpineyu.fel.parser.FelNode;

import java.util.*;

public class Example {
 
	public static void main(String[] args) {
 
		System.out.println("-----------1.入门---------");
		helloworld();
 
		System.out.println("-----------2.使用变量---------");
		useVariable();
 
		System.out.println("-----------3.获取对象属性---------");
		getAttr();
 
		System.out.println("---------4.调用对象的方法-----------");
		callMethod();
 
		System.out.println("--------5.访问数组、集合------------");
		visitColl();
 
		System.out.println("--------6.自定义上下文环境------------");
		// context();
 
		System.out.println("--------7.多层次上下文环境(变量命名空间)------------");
		contexts();
 
		System.out.println("---------8.大数值计算-----------");
		testBigNumber();
 
		System.out.println("----------9.函数----------");
		userFunction();
 
		System.out.println("---------10.自定义 解释器-----------");
		userInterpreter();
 
		System.out.println("----------11.操作符重载----------");
		operatorOverload();
 
		System.out.println("----------12.速度测试----------");
		testSpeed();
 
		System.out.println("----------13.静态方法----------");
		staticMethod();
	}
 
	/**
	 * 入门
	 */
	public static void helloworld() {
		// FelEngine fel = new FelEngineImpl();
		Object result = FelEngine.instance.eval("5000*12+7500");
		System.out.println(result);
	}
 
	/**
	 * 使用变量
	 */
	public static void useVariable() {
		FelEngine fel = getEngine();
		FelContext ctx = fel.getContext();
		ctx.set("单价", 5000);
		ctx.set("数量", 12);
		ctx.set("运费", 7500);
		Object result = fel.eval("单价*数量+运费");
		System.out.println(result);
	}
 
	/**
	 * 获取对象属性
	 */
	public static void getAttr() {
		FelEngine fel = getEngine();
		FelContext ctx = fel.getContext();
		Foo foo = new Foo();
		ctx.set("foo", foo);
		Map<String, String> m = new HashMap<>();
		m.put("ElName", "fel");
		ctx.set("m", m);
 
		// 调用foo.getSize()方法。
		Object result = fel.eval("foo.size");
		System.out.println(result);
		// 调用foo.isSample()方法。
		result = fel.eval("foo.sample");
		System.out.println(result);
		// foo没有name、getName、isName方法
		// foo.name会调用foo.get("name")方法。
		result = fel.eval("foo.name");
		System.out.println(result);
		// m.ElName会调用m.get("ElName");
		result = fel.eval("m.ElName");
		System.out.println(result);
	}
 
	/**
	 * 调用对象的方法
	 */
	public static void callMethod() {
		FelEngine fel = getEngine();
		FelContext ctx = fel.getContext();
		ctx.set("out", System.out);
		fel.eval("out.println('Hello Everybody'.substring(6))");
	}
 
	/**
	 * 访问数组、集合
	 */
	public static void visitColl() {
		FelEngine fel = getEngine();
		FelContext ctx = fel.getContext();
 
		// 数组
		int[] intArray = { 1, 2, 3 };
		ctx.set("intArray", intArray);
		// 获取intArray[0]
		String exp = "intArray[0]";
		System.out.println(exp + "->" + fel.eval(exp));
 
		// List
		List<Integer> list = Arrays.asList(1, 2, 3);
		ctx.set("list", list);
		// 获取list.get(0)
		exp = "list[0]";
		System.out.println(exp + "->" + fel.eval(exp));
 
		// 集合
		Collection<String> coll = Arrays.asList("a", "b", "c");
		ctx.set("coll", coll);
		// 获取集合最前面的元素。执行结果为"a"
		exp = "coll[0]";
		System.out.println(exp + "->" + fel.eval(exp));
 
		// 迭代器
		Iterator<String> iterator = coll.iterator();
		ctx.set("iterator", iterator);
		// 获取迭代器最前面的元素。执行结果为"a"
		exp = "iterator[0]";
		System.out.println(exp + "->" + fel.eval(exp));
 
		// Map
		Map<String, String> m = new HashMap<String, String>();
		m.put("name", "Wangxiaoming");
		ctx.set("map", m);
		exp = "map.name";
		System.out.println(exp + "->" + fel.eval(exp));
 
		// 多维数组
		int[][] intArrays = { { 11, 12 }, { 21, 22 } };
		ctx.set("intArrays", intArrays);
		exp = "intArrays[0][0]";
		System.out.println(exp + "->" + fel.eval(exp));
 
		// 多维综合体，支持数组、集合的任意组合。
		List<int[]> listArray = new ArrayList<int[]>();
		listArray.add(new int[] { 1, 2, 3 });
		listArray.add(new int[] { 4, 5, 6 });
		ctx.set("listArray", listArray);
		exp = "listArray[0][0]";
		System.out.println(exp + "->" + fel.eval(exp));
	}
 
	/**
	 * 自定义上下文环境
	 */
	public static void context() {
		// 负责提供气象服务的上下文环境
		FelContext ctx = new AbstractContext() {
			@Override
			public Object get(String name) {
				if ("天气".equals(name)) {
					return "晴";
				}
				if ("温度".equals(name)) {
					return 25;
				}
				return null;
			}
 
		};
		FelEngine fel = new FelEngineImpl(ctx);
		String exp = "'天气-----:'+天气+';温度------:'+温度";
		Object eval = fel.compile(exp, ctx).eval(ctx);
		System.out.println(eval);
	}
 
	/**
	 * 多层次上下文环境(变量命名空间)
	 */
	public static void contexts() {
		FelEngine fel = getEngine();
		String costStr = "成本";
		String priceStr = "价格";
		FelContext baseCtx = fel.getContext();
		// 父级上下文中设置成本和价格
		baseCtx.set(costStr, 50);
		baseCtx.set(priceStr, 100);
 
		String exp = priceStr + "-" + costStr;
		Object baseCost = fel.eval(exp);
		System.out.println("期望利润：" + baseCost);
 
		FelContext ctx = new ContextChain(baseCtx, new MapContext());
		// 通货膨胀导致成本增加（子级上下文 中设置成本，会覆盖父级上下文中的成本）
		ctx.set(costStr, 50 + 20);
		Object allCost = fel.eval(exp, ctx);
		System.out.println("实际利润：" + allCost);
	}
 
	/**
	 * 大数值计算
	 */
	public static void testBigNumber() {
		// 构建大数值计算引擎
		FelEngine fel = FelBuilder.bigNumberEngine();
		String input = "111111111111111111111111111111+22222222222222222222222222222222";
		Object value = fel.eval(input);// 解释执行
		Object compileValue = fel.compile(input, fel.getContext()).eval(
				fel.getContext());// 编译执行
		System.out.println("大数值计算（解释执行）:" + value);
		System.out.println("大数值计算（编译执行）:" + compileValue);
	}
 
	/**
	 * 函数
	 */
	public static void userFunction() {
		// 定义hello函数
		Function fun = new CommonFunction() {
 
			@Override
			public String getName() {
				return "hello";
			}
 
			/*
			 * 调用hello("xxx")时执行的代码
			 */
			@Override
			public Object call(Object[] arguments) {
				Object msg = null;
				if (arguments != null && arguments.length > 0) {
					msg = arguments[0];
				}
				return ObjectUtils.toString(msg);
			}
 
		};
		FelEngine e = getEngine();
		// 添加函数到引擎中。
		e.addFun(fun);
		String exp = "hello('fel')";
		// 解释执行
		Object eval = e.eval(exp);
		System.out.println("hello " + eval);
		// 编译执行
		Expression compile = e.compile(exp, null);
		eval = compile.eval(null);
		System.out.println("hello " + eval);
	}
 
	/**
	 * 
	 */
	public static void testCompileX() {
		FelEngine fel = getEngine();
		String exp = "单价*数量";
		final MutableInt index = new MutableInt(0);
 
		// 数据库中单价列的记录
		final int[] price = new int[] { 2, 3, 4 };
		// 数据库中数量列的记录
		final double[] number = new double[] { 10.99, 20.99, 9.9 };
		FelContext context = new AbstractContext() {
 
			@Override
			public Object get(String name) {
				if ("单价".equals(name)) {
					return price[index.intValue()];
				}
				if ("数量".equals(name)) {
					return number[index.intValue()];
				}
				return null;
			}
		};
		Expression compExp = fel.compile(exp, context);
		for (int i = 0; i < number.length; i++) {
			index.setValue(i);
			Object eval = compExp.eval(context);
			System.out.println("总价[" + price[i] + "*" + number[i] + "=" + eval
					+ "]");
		}
	}
 
	/**
	 * 自定义 解释器
	 */
	public static void userInterpreter() {
		FelEngine fel = getEngine();
		String costStr = "成本";
		FelContext rootContext = fel.getContext();
		rootContext.set(costStr, "60000");
		FelNode node = fel.parse(costStr);
		// 将变量解析成常量
		node.setInterpreter(new ConstInterpreter(rootContext, node));
		System.out.println(node.eval(rootContext));
	}
 
	/**
	 * 操作符重载，使用自定义解释器实现操作符重载
	 */
	public static void operatorOverload() {
		/*
		 * 扩展Fel的+运算符，使其支持数组+数组
		 */
 
		FelEngine fel = getEngine();
		// 单价
		double[] price = new double[] { 2, 3, 4 };
		// 费用
		double[] cost = new double[] { 0.3, 0.3, 0.4 };
		FelContext ctx = fel.getContext();
		ctx.set("单价", price);
		ctx.set("费用", cost);
		String exp = "单价+费用";
		Interpreters interpreters = new Interpreters();
		// 定义"+"操作符的解释方法。
		interpreters.add("+", new Interpreter() {
			@Override
			public Object interpret(FelContext context, FelNode node) {
				List<FelNode> args = node.getChildren();
				double[] leftArg = (double[]) args.get(0).eval(context);
				double[] rightArg = (double[]) args.get(1).eval(context);
				return sum(leftArg) + sum(rightArg);
			}
 
			// 对数组进行求和
			public double sum(double[] array) {
				double d = 0;
				for (int i = 0; i < array.length; i++) {
					d += array[i];
				}
				return d;
			}
		});
 
		// 使用自定义解释器作为编译选项进行进行编译
		Expression expObj = fel.compile(exp, null, interpreters);
		Object eval = expObj.eval(ctx);
		System.out.println("数组相加:" + eval);
	}
 
	/**
	 * 速度测试
	 */
	public static void testSpeed() {
		FelEngine fel = getEngine();
		String exp = "40.52334+60*(21.8144+17*32.663)";
		FelNode node = fel.parse(exp);
		int times = 1000;
		long s1 = System.currentTimeMillis();
		for (int i = 0; i < times; i++) {
			// double j = 40.52334 + 60 * (21.8144 + 17 * 32.663);
			node.eval(null);
		}
		long s2 = System.currentTimeMillis();
		System.out.println("花费的时间:" + (s2 - s1));
	}
 
	/**
	 * 大数据量计算（计算1千万次)
	 */
	public static void massData() {
		FelEngine fel = getEngine();
		final Interpreters opti = new Interpreters();
		final MutableInt index = new MutableInt(0);
		int count = 10 * 1000 * 1000;
		final double[] counts = new double[count];
		final double[] prices = new double[count];
		Arrays.fill(counts, 10d);
		Arrays.fill(prices, 2.5d);
		opti.add("单价", (context, node) -> prices[index.intValue()]);
		opti.add("数量", (context, node) -> counts[index.intValue()]);
		Expression expObj = fel.compile("单价*数量", null, opti);
		long start = System.currentTimeMillis();
		Object result = null;
		for (int i = 0; i < count; i++) {
			result = expObj.eval(null);
			index.increment();
		}
		long end = System.currentTimeMillis();
 
		System.out.println("大数据量计算:" + result + ";耗时:" + (end - start));
	}
 
	/**
	 * 静态方法
	 * 
	 * 
	 * 如果你觉得上面的自定义函数也麻烦，Fel提供的$函数可以方便的调用工具类的方法 熟悉jQuery的朋友肯定知道"$"函数的威力。
	 * Fel东施效颦，也实现了一个"$"函数,其作用是获取class和创建对象。结合点操作符，可以轻易的调用工具类或对象的方法。
	 * 通过"$('class').method"形式的语法，就可以调用任何等三方类包（commons lang等）及自定义工具类的方法，
	 * 也可以创建对象，调用对象的方法。如果有需要，还可以直接注册Java Method到函数管理器中。
	 */
	public static void staticMethod() {
		// 调用Math.min(1,2)
		System.out.println(FelEngine.instance.eval("$('Math').max(1,3)"));
		// 调用new Foo().toString();
		System.out.println(FelEngine.instance
				.eval("$('com.ebiz.fel.Foo.new').toString()"));
	}
 
	private static FelEngine getEngine() {
		return FelBuilder.engine();
	}
 
}
 
class ColumnInterpreter implements Interpreter {
	MutableInt index;
 
	double[] records;
 
	ColumnInterpreter(MutableInt index, double[] records) {
		this.index = index;
		this.records = records;
	}
 
	@Override
	public Object interpret(FelContext context, FelNode node) {
		return records[index.intValue()];
	}
}
 
class MutableInt {
	private int value;

	public MutableInt(int i) {
		this.value = i;
	}

	public int intValue() {
		return value;
	}

	public void setValue(int i) {
		this.value = i;
	}

	public void increment() {
		value++;
	}
}
