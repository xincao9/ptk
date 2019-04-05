# ptk (pressure test kit)
Easy to use java based, stress test suite [中文说明](https://github.com/xincao9/ptk/wiki/%E4%B8%AD%E6%96%87%E4%BD%BF%E7%94%A8%E8%AF%B4%E6%98%8E)

![logo](https://github.com/xincao9/ptk/blob/master/logo.png)

**_Features_**

* Small and flexible, without any third party dependence
* Scalability is good, you can customize the data source
* The pressure measurement code is easy to manage in one project, and the pressure measurement method is specified according to the command during pressure measurement.
* After the long-term use of the project, the accuracy of the pressure measurement is high
* Packaged as an executable jar for easy use anywhere

**_Maven dependency_**

```
<dependency>
    <groupId>com.github.xincao9</groupId>
    <artifactId>ptk-core</artifactId>
    <version>1.0</version>
</dependency>
```
**_com.github.xincao9.ptk.core.Source_**

Data source: A collection of data for testing. By default, Source implements FileSource and SequenceSource

If custom

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
            Worker.submit(new D(i)); // Write to the pressure data pool
        }
        return 5000;
    }

}
```

**_com.github.xincao9.ptk.core.Method_**

Test method: code block for pressure measurement

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

**_Startup method_**

```
PTKCore.bootstrap(new SourceD(), args);
```

**_Command line execution_**

```
java -jar ptk-sample/target/ptk-sample-1.0.jar
1.cmd -[c, t, m] value
2.com.github.xincao9.ptk.core.Source interface must be implemented, implemented as a read data source
The 3.com.github.xincao9.ptk.core.Method interface must be implemented and needs to be identified by the @Test identifier as the code block to be tested.
4.com.github.xincao9.ptk.core.Result interface does not have to be implemented, it can output test results to its own system
5.-c Concurrency limit 0 < concurrent <= 1024 Default CPU core number
6.-t request delay limit cd > 0 default 50ms; it is recommended to block the call to set a small point, calculate the dense call to set a large point, less than 0 for never delay
7.-m test method class

java -jar ptk-sample/target/ptk-sample-1.0.jar -m MethodD -c 2 -t -1
```


#### Contact

* [issues](https://github.com/xincao9/ptk/issues)
* xincao9@gmail.com
