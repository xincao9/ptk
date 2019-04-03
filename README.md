# ptk (pressure test kit)
Easy to use java based, stress test suite

**_Maven dependency_**

```
<dependency>
    <groupId>com.github.xincao9</groupId>
    <artifactId>ptk-core</artifactId>
    <version>1.0</version>
</dependency>
```
**_com.github.xincao9.ptk.core.Source_**

数据源：用于测试的数据集合。默认，Source 实现 FileSource和SequenceSource

如果自定义

```
public class D {

    private final int id;

    public D(int id) {
        this.id = id;
    }

    public int getId() {
        return this.id;
    }
}
public class SourceD implements Source {

    @Override
    public int read() {
        for (int i = 1; i < 5000; i++) {
            Worker.submit(new D(i)); // 写入到数据压测数据池
        }
        return 5000;
    }

}
```

**_com.github.xincao9.ptk.core.Method_**

测试方法：用于压测的代码块

```
@Test(name = "MethodD")
public class MethodD extends Method {

    @Override
    public void exec(Object params) {
        D d = (D) params;
        Logger.info(d.getId());
    }

}
```

**_启动方法_**

```
PTKCore.bootstrap(new SourceD(), args);
```

**_命令行执行_**

```
java -jar ptk-sample/target/ptk-sample-1.0.jar
1.cmd -[c, t, m] value
2.com.github.xincao9.ptk.core.Source 接口必须实现, 实现为读取数据源
3.com.github.xincao9.ptk.core.Method 接口必须实现且需要使用@Test 标识, 实现为需要测试的代码块
4.com.github.xincao9.ptk.core.Result 接口不必须实现, 通过它可以将测试结果输出到自己的系统中
5.-c 并发数限制 0 < concurrent <= 1024 默认 1
6.-t 请求延时限制 cd > 0 默认 50ms; 建议阻塞调用设置小点, 计算密集调用设置大点, 小于0 为永不延时
7.-m 测试的方法类

java -jar ptk-sample/target/ptk-sample-1.0.jar -m MethodD -c 2 -t -1
```


#### Contact

* [issues](https://github.com/xincao9/ptk/issues)
* xincao9@gmail.com
