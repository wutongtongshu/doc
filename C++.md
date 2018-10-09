# 1 基础

## 1.1 const constexpr

- const: meaning roughly ‘‘I promise not to change this value’’ . This is used primarily

to specify interfaces(接口使用), so that data can be passed to functions without fear of it being modified.
The compiler enforces the promise made by const.

- constexpr: meaning roughly ‘‘to be evaluated at compile time（编译时）’’ . This is used primarily

to specify constants, to allow placement of data in memory where it is unlikely to be corrupted,
and for performance.

## 1.2 implementation-defined 

国外学者用的，implementation 可以认为是编译器，或者实现

## 1.3 初始化列表

初始化列表，提供了检查的功能，不允许 narrowing 操作，可以扩展操作

```c++
bool b_test{7} 这个编译报错，原因是，narrowing。窄化
error C2397: 从“int”转换到“bool”需要收缩转换
```

## 1.4 基本数据类型

### 1.4.1 bool

true 的值为 1， false 值 为 0 。非零的值都认为是 1 。

```c++
bool b1 = 7; // 7!=0, so b becomes true
bool b2 {7}; // error : narrowing 做了检查的，编译不过

```

### 1.4.2 字符型

`char`：我在 win 10 上测试，超过 128 会出问题

`signed char`：[-128, 127]  

`unsigned char`:[0, 255]

`wchar_t`: Provided to hold characters of a larger character set such as Unicode (see §7.3.2.2).
The size of wchar_t is implementation-defined and large enough to hold the largest character
set supported by the implementation’s locale (Chapter 39).
`char16_t`: A type for holding 16-bit character sets, such as UTF-16.
`char32_t`: A type for holding 32-bit character sets, such as UTF-32.

### 1.4.3 整形

`int`:  `long int`:   `long long int`:

### 1.4.4 浮点型

`float`:   `double`:   `long double`:

### 1.4.5 struct

struct有默认构造函数，一旦定义了非空构造函数，必须使用构造函数，否则无法默认构造。

```C++
struct Address{
	const char* name;
	int number;
	const char* street;
	const char* town;
	char state[2];
	const char* zip;
};

Address aj = {
		"Jim Dandy", 61, "South St",
		"new pro", { 'N', 'J' }, "09747"
	};

Address aj2;
aj2.name = "wudeyun";
aj2.number = 12;
```



## 1.5 undefined  unspecified 

 undefined 通常是那些语法正确,但行为未知,且不可预知(或行为缺乏广泛性)的东西.比如a[-1]的值.使用已经析构的对象等等.

 unspecified 通常是满足了标准需求以外的行为.比如类的布局,虚函数的实现方式,一些表达式的计算顺序等等. 

## 1.6 与 C 兼容

每个 C 标准库头文件，都有一个与之兼容的 C++ 头文件。比如 stdio.h 对应的是 cstdio

`cstdio`文件中：将 printf 定义在 std 空间中。

```c++

```

为了兼容 C ， `stdio.h` 中

```c++
#ifdef __cplusplus // for C++ compilers only (§15.2.5)
namespace std { // the standard librar y is defined in namespace std (§4.1.2)
extern "C" { // stdio functions have C linkage (§15.2.5)
#endif
/* ... */
int printf(const char∗, ...);
/* ... */
#ifdef __cplusplus
}
}
// ...
using std::printf; // make printf available in global namespace
// ...
#endif
```

这样，当我们引用 stdio.h 后，实际的实现是 cstdio 实现的， stdio.h 只是做了一层包装，从而打到兼容 C 程序的目的

想这样的情况:一个库文件已经用C写好了而且运行得很良好，这个时候我们需要使用这个库文件，但是我们需要使用C++来写这个新的代码。如果这个代码使用的是C++的方式链接这个C库文件的话，那么就会出现链接错误.我们来看一段代码:首先，我们使用C的处理方式来写一个函数，也就是说假设这个函数当时是用C写成的:

```c
//f1.c
void f1() {return; }
```

编译命令是:gcc -c f1.c -o f1.o 产生了一个叫f1.o的库文件。再写一段代码调用这个f1函数:

//这个extern表示f1函数在别的地方定义，这样可以通过编译，但是链接的时候还是需要链接上原来的库文件.

```c++
// test.cpp
extern void f1();

int main(){ 
    f1();
	return 0;
}
```

通过gcc -c test.cxx -o test.o 产生一个叫test.o的文件。然后，我们使用gcc test.o f1.o来链接两个文件，可是出错了，错误的提示是:

test.o(.text + 0x1f):test.cpp: undefine reference to 'f1()'

也就是说，在编译test.cxx的时候编译器是使用C++的方式来处理f1()函数的，但是实际上链接的库文件却是用C的方式来处理函数的，所以就会出现链接过不去的错误:因为[链接器](https://baike.baidu.com/item/%E9%93%BE%E6%8E%A5%E5%99%A8)找不到函数。

因此，为了在C++代码中调用用C写成的库文件，就需要用extern "C"来告诉编译器:这是一个用C写成的库文件，请用C的方式来链接它们。

比如，现在我们有了一个C库文件，它的头文件是f.h，产生的lib文件是f.lib，那么我们如果要在C++中使用这个库文件，我们需要这样写:

```c++
extern "C"
{
	#include "f.h"
}
```

回到上面的问题，如果要改正链接错误，我们需要这样子改写test.cxx:

```c++
extern "C"
{
	extern void f1();
}

int main()
{
	f1();
	return 0;
}
```

重新编译并且链接就可以过去了.

## 1.7 单一定义规则 

An object must be defined exactly once in a program. It may be declared many times, but
the types must agree exactly

c++在链接的过程中，会对符号进行重定向，多次定义一个变量，会造成重复定义，如下 x 被定义了两次，b 申明的类型不一样，c 没有申明。

```c++
//file1.cpp
    int x = 1;
	int b = 1;
	extern int c;

//file2.cpp
	int x;
	extern double b;
	extern int c;
```

## 1.8 JAVA 和 C++ 区别

- java 所有变量都定义在一个范围类，或者所有的变量都在类的范围中定义
- \#include 跟 import 不一样， import 只能用一次， #include 可以多次导致重复
- C++ 单一定义原则是妥协的，类的定义本身就违反了 c 的单一定义原则， 基本类型只能申明一次，但是 类可以多次定义。
- c++ 的static、const表示内联，而java 得static表示全局变量，恰恰相反
- c++类中的static表示共有，static函数表示共有

## 1.9 先申明，后使用

```c++
// file1.cpp:
int g() { return f()+7; } // error : f() not (yet) declared
int f() { return x; } // error : x not (yet) declared
int x;
```

## 1.10 内部链接，外部链接

这个我的理解是与 **链接** 紧密相连的，内部链接的意思是链接的时候，使用内部变量，外部链接是使用外部变量

const 和 static 表示内联，是不能使用 extern 引用的，如下都是错误的。

```c++
//file1.cpp
	static int x = 1;
	const int y = 1;

//file2.cpp
	extern int x;
	extern const int y;

//但是下面这个却是正确的
// file1.cpp:
extern const int a = 77;
// file2.cpp:
extern const int a;
```

对于使用 **inline** 关键字的函数，An inline function must be defined identically（完全一样） in every translation unit（编译单元） in which it is used，inline[关键字](https://baike.baidu.com/item/%E5%85%B3%E9%94%AE%E5%AD%97)用来定义一个类的[内联函数](https://baike.baidu.com/item/%E5%86%85%E8%81%94%E5%87%BD%E6%95%B0)，引入它的主要原因是用它替代C中[表达式](https://baike.baidu.com/item/%E8%A1%A8%E8%BE%BE%E5%BC%8F)形式的[宏定义](https://baike.baidu.com/item/%E5%AE%8F%E5%AE%9A%E4%B9%89)。 inline只有在定义的时候使用，而不是申明的时候使用。内联函数会自动展开，过大的函数不适合。内联的函数不能被 extern 引用

```c++
// file1.cpp:
inline int f(int i) { return i; }
// file2.cpp:
inline int f(int i) { return i+1; }
```

这个违反了完全一致的原则，所以使用的时候，inline 函数最好放在 .h 文件中定义，这样就会正常

## 1.11 引用

引用和普通变量是严格区分的，引用赋值给普通变量是复制赋值。

# 2 抽象机制

## 2.1 copy 初始化

```c++
Date d1 = my_bir thday; // initialization by copy
Date d2 {my_bir thday}; // initialization by copy
```

## 2.2 构造函数

使用 init 函数初始化对象存在的问题是，可能忘了初始化，可能初始化了多次。最好的办法就是显示调用一个函数，专门来做这个事，这个函数就是 constructor 

```c++
class Date {
	int d, m, y;
public:
	Date(int dd, int mm, int yy); // constructor
};

//两种构造
Date today = Date {23,6,1983};  //这种是不是有个临时对象？
Date xmas {25,12,1990}; // 上面的简写
Date xmas(25,12,1990); // 上面的简写
```

类对象如果有无参构造，会默认调用

```c++
Date::Date(){
	this->d = 1;
	this->m = 1;
	this->y = 1;
}

Date tt;
std::cout << tt.d << '\n' << tt.m << '\n' << tt.y << std::endl;
输出 3 个 1, 这是调用了默认的构造函数
```

默认参数值

```c++
//test.h
public:
	Date(int dd = 0, int mm = 1, int yy = 0);

//test.cpp
    Date::Date(int d, int m, int y){
	this->d = d;
	this->m = m;
	this->y = y;
}

//xxx.cpp
	Date tt{4};
	std::cout << tt.m << tt.yy << std::endl;
//输出 10 ，注意，实现的时候并没有把默认值带上，但是编译器可以识别，直接给了默认值
```

显示构造函数，explicit，这个关键字，使得可以不能构造函数，所谓隐式构造，如

```c++
//隐式构造
//test.h
public:
	Date(int, int, int);
//test.cpp
    Date::Date(int d, int m, int y){...}
//xxx.cpp
	Data day = {1, 2, 3}
```

这里是隐式构造，可以调试，可以确定调用了上述构造函数。但是如果使用关键字 

```c++
//test.h
public:
	explicit Date(int, int, int);
```

则这种无效，所以可以肯定的是， struct 的初始化列表是使用了隐式构造函数，下面来验证

```c++
//test.h
struct X{
	int a;
	explicit X();
	X(int a);
};
//test.cpp
X::X(int a){
	this->a = a;
}
X::X(){}

//xxxx.cpp
X y = {1}; 单参数构造函数，没有 explicit 修饰，可以隐式构造
X z;       这个不叫隐式构造，这个是显示调用
X s = {};  这个是真正的隐式构造，但是无参构造有 explicit 修饰，无法完成隐式构造。
```

## 2.3 copy move

传值，逻辑上两种不同选择:
• Copy is the conventional meaning of x=y; that is, the effect is that the values of x and y are
both equal to y’s value afterthe assignment.
• Move leaves x with y’s former value and y with some **moved-from state**. For the most interesting
cases, containers, that moved-from state is ‘‘empty.’’

### 2.3.1 copy

Copy for a class X is defined by two operations:
• Copy constructor: X(const X&)
• Copy assignment: X& operator=(const X&)



```c++
std::string fun_one(std::string vm)
{
	return vm;
}

std::string real = "real param";
std::string result = fun_one(real);
```

result 取得 "real param" 这个值，并不是那么显然。首先， 实参 real 拷贝临时 变量到 形参 vm 。在函数返回时，生成临时变量，把值再拷贝给 result 。这经过了两次拷贝，所以是很复杂的。下面分析一个例子

```c++
string ident(string arg){ return arg; }

int main ()
{
string s1 {"Adams"};
s1 = indet(s1);
string s2 {"Pratchett"};
s1 = s2;
}
```

这段代码所用到的函数如下:

• A constructor initializing a string with a string literal (构造了 s1 和 s2)
• A copy constructor copying a string (into the function argument arg)
• A move constructor moving the value of a string (from arg out of ident() into a temporary
variable holding the result of ident(s1))
• A move assignment moving the value of a string (from the temporary variable holding the
result of ident(s1) into s1)
• A copy assignment copying a string (from s2 into s1)
• A destructor releasing the resources owned by s1, s2, and the temporary variable holding the
result of ident(s1)

## 2.4 左值 右值

```c++
// 规则一：非const左值引用只能绑定到非const左值
A &lvalue_reference1 = lvalue;         // ok
A &lvalue_reference2 = const_lvalue;   // error
A &lvalue_reference3 = rvalue();       // error
A &lvalue_reference4 = const_rvalue(); // error

// 规则二：const左值引用可绑定到const左值、非const左值、const右值、非const右值
const A &const_lvalue_reference1 = lvalue;         // ok
const A &const_lvalue_reference2 = const_lvalue;   // ok
const A &const_lvalue_reference3 = rvalue();       // ok
const A &const_lvalue_reference4 = const_rvalue(); // ok

// 规则三：非const右值引用只能绑定到非const右值
A &&rvalue_reference1 = lvalue;         // error
A &&rvalue_reference2 = const_lvalue;   // error
A &&rvalue_reference3 = rvalue();       // ok
A &&rvalue_reference4 = const_rvalue(); // error

// 规则四：const右值引用可绑定到const右值和非const右值，不能绑定到左值
const A &&const_rvalue_reference1 = lvalue;         // error
const A &&const_rvalue_reference2 = const_lvalue;   // error
const A &&const_rvalue_reference3 = rvalue();       // ok
const A &&const_rvalue_reference4 = const_rvalue(); // ok

// 规则五：函数类型例外
void fun() {}
typedef decltype(fun) FUN;  // typedef void FUN();
FUN       &  lvalue_reference_to_fun       = fun; // ok
const FUN &  const_lvalue_reference_to_fun = fun; // ok
FUN       && rvalue_reference_to_fun       = fun; // ok
const FUN && const_rvalue_reference_to_fun = fun; // ok
```

## 2.5 重载（overloading）和重写（override）

重载，是同一个类的两个函数

override，是覆盖的意思，所以是子类与父类的关系



# 4 linux c/c++

## 4.1 gcc编译 c 程序

```
    1.预处理，生成预编译文件（.文件）：

        Gcc –E hello.c –o hello.i
    2.编译，生成汇编代码（.s文件）：

        Gcc –S hello.i –o hello.s
    3.汇编，生成目标文件（.o文件）：
        Gcc –c hello.s –o hello.o
    4.链接，生成可执行文件：
        Gcc hello.o –o hello
```

set noexpandtab 

### 4.1.1 gcc 语法

| 后缀名    | 所对应的语言                                                 |
| --------- | ------------------------------------------------------------ |
| -c        | 只是编译不链接，生成目标文件“.o”                             |
| -S        | 只是编译不汇编，生成汇编代码                                 |
| -E        | 只进行预编译，不做其他处理                                   |
| -g        | 在可执行程序中包含标准调试信息                               |
| -o file   | 把输出文件输出到file里                                       |
| -v        | 打印出编译器内部编译各过程的命令行信息和编译器的版本         |
| -I dir    | 在头文件的搜索路径列表中添加 dir 目录，不要包含文件名，是路径 |
| -L dir    | 在库文件的搜索路径列表中添加 dir 目录                        |
| -static   | 链接静态库                                                   |
| -llibrary | 连接名为library的库文件                                      |

## 4.2 makefile

### 4.2.1 没有头文件

```c
//count_words.c
#include <stdio.h>
#include <stdlib.h>

extern int fee_count, fie_count, foe_count, fum_count;
extern int yylex( void );
int main( int argc, char ** argv )
{
    yylex();
    printf( "%d %d %d %d\n", fee_count, fie_count, foe_count, fum_count );
    exit( 0 );
}
```

tab 键不能去掉，否则编译错误

```c
//lexer.l
	int fee_count = 0;
	int fie_count = 0;
	int foe_count = 0;
	int fum_count = 0;
%%
fee	fee_count++;
fie	fie_count++;
foe	foe_count++;
fum	fum_count++;
.
\n
```

命令用的tab键

```makefile
//makefile
count_words: count_words.o lexer.o -lfl
	gcc count_words.o lexer.o -lfl -o count_words
count_words.o: count_words.c
	gcc -c count_words.c
lexer.o: lexer.c
	gcc -c lexer.c
lexer.c: lexer.l
	flex -t lexer.l > lexer.c

```

运行结果:

```shell
./count_words << lexer.l
3 3 3 3 
```

### 4.2.2 有头文件

test 工程目录结构

>include
>
>>counter.h
>>
>>lexer.h
>
>src
>
>>count_words.c
>>
>>counter.c
>>
>>lexer.l
>
>makefile



如下是源代码：

```c
//lexer.h
#ifndef LEXER_H_
#define LEXER_H_
extern int fee_count, fie_count, foe_count, fum_count;
extern int yylex( void );
#endif

//lexer.l
	int fee_count = 0;
	int fie_count = 0;
	int foe_count = 0;
	int fum_count = 0;
%%
fee     fee_count++;
fie     fie_count++;
foe     foe_count++;
fum     fum_count++;
.
\n

//counter.h
#ifndef COUNTER_H_
#define COUNTER_H_
extern void counter(int args[4]);
#endif

//counter.c
#include <lexer.h>
#include <counter.h>
 
void counter( int counts[4]) {
        yylex();
        counts[0] = fee_count;
        counts[1] = fie_count;
        counts[2] = foe_count;
        counts[3] = fum_count;
}

//count_words.c
#include <stdio.h>
#include <lexer.h>
#include <counter.h>

int main( int argc, char ** argv ){
	int temp[4];
	int* p = temp;
	counter(p);
    printf( "%d %d %d %d\n", *p, *(p+1), *(p+2), *(p+3) );
    return( 0 );
}

//makefile
VPATH=src include
CC = gcc -g
CPPFLAGS = -I include
count_words: count_words.o counter.o lexer.o -lfl
	$(CC) $^ -o $@
count_words.o: count_words.c counter.h lexer.h
	$(CC) $(CPPFLAGS) -c $< -o $@ 
counter.o: counter.c counter.h lexer.h
	$(CC) $(CPPFLAGS) -c $< -o $@
lexer.o: lexer.c lexer.h
	$(CC) $(CPPFLAGS) -c $< -o $@
lexer.c: lexer.l
	flex -t $< > $@
.PHONY: clean
clean: 
	rm *.o lexer.c count_words
```

make

```shell
gcc -g -I include -c src/count_words.c -o count_words.o 
gcc -g -I include -c src/counter.c -o counter.o
flex -t src/lexer.l > lexer.c
gcc -g -I include -c lexer.c -o lexer.o
gcc -g count_words.o counter.o lexer.o /usr/lib/x86_64-linux-gnu/libfl.so -o count_words
```

makefile中  **VPATH** 可以换一种写法

```makefile
vpath %.l %.c src
vpath %.h include
```

### 4.2.3 规则

% 代表 通配符（注意不是正则表达式）。一个工作目标以分为前后缀。-

- 静态模式规则

  ```makefile
  $(OBJECTS):%.o:%.c
      gcc -c $< -o $@
  ```

  \$(OBJECTS) 是目标文件列表，%.o 模式匹配 \$(OBJECTS) 中目标文件名，目标文件名替换进 %.c，得到必要条件。

- 后缀规则

  老系统才用后缀规则，新系统一般不用。

  ```makefile
  .c.o: 等价于 %.o:%.c
  ```

- 单后缀规则

  ```makefile
  .p: 等价于 %:%.p
  ```

- 扩展名列表

  ```makefile
  .SUFFIXES: .out .a .ln .c
  .SUFFIXES: .pdf .fo .html .xml
  ```

- 隐含规则

  GUN make 3.8  具有 90 多个隐含规则，涉及各种编程语言。隐含规则是上述规则的特殊情况。

### 4.2.4 规则的结构

```makefile
%.o:%.c
    $(COMPILE.c) $(OUTPUT_OPTION) $<
COMPILE.c = $(CC) $(CFLAGS) $(CPPFLAGS) $(TARGET_ARCH) -C
CC = GCC
OUTPUT_OPTION = -O $@
```

CFLAGS : 编译选项

CPPFLAGS： 预编译选项

## 4.3 一目标多规则

###4.3.1 例一

创建：

```shell
mkdir oneTarget
touch oneTarget/a.h
touch oneTarget/b.h
touch oneTarget/b.h
```

创建 Makefile 文件

```makefile
all:test
test:a.h
	@echo "this is a.h"
test:b.h
	@echo "this is b.h"
test:c.h
	@echo "this is c.h"
```

执行 make 命令，输出如下：

```shell
Makefile:5: warning: overriding recipe for target 'test'
Makefile:3: warning: ignoring old recipe for target 'test'
Makefile:7: warning: overriding recipe for target 'test'
Makefile:5: warning: ignoring old recipe for target 'test'
this is c.h
```

不管执行多少次，都是这个输出结果。原因是，分析 all  目标时发现依赖 test 所以要先执行 test 目标。这样 第 5 行和第 7 行 的脚本不能执行，原因时 target 重复，所以报 overriding 。分析完成后开始执行，从上往下分析，子下往上执行，先执行 第 9 行，输出 this is c.h ，第 5 行 和 第 3 行 由于 test 目标已执行，直接输出 ignoring。每次执行， test  都会被执行，原因时不存在 test 这个文件，就没法判断 test 的时间。

### 4.3.2 例二

在例一中，由于不产生 test 文件，导致每次都会执行 test 目标，现在修改 Makefile 文件，如下

```makefile
all:test
test:a.h
	@echo "this is a.h"
test:b.h
	@echo "this is b.h"
test:c.h
	@echo "this is c.h"
	touch test
```

首次执行 make，输出

```shell
Makefile:5: warning: overriding recipe for target 'test'
Makefile:3: warning: ignoring old recipe for target 'test'
Makefile:7: warning: overriding recipe for target 'test'
Makefile:5: warning: ignoring old recipe for target 'test'
this is c.h
touch test
```

生成了 test 文件，第二次执行 make 输出

```shell
Makefile:5: warning: overriding recipe for target 'test'
Makefile:3: warning: ignoring old recipe for target 'test'
Makefile:7: warning: overriding recipe for target 'test'
Makefile:5: warning: ignoring old recipe for target 'test'
make: Nothing to be done for 'all'.
```

第二次什么都没做，原因是 第一次生成的 test 文件是最新的。接下来我们更新 a.h 文件

```shell
echo "aaaa" > a.h
```

由于 a.h 的更新，导致 test 目标被执行，为什么不输出  "this is a.h" 呢，原因是 make 只能判断出 test 目标要更新，但更新规则还是自下而上的。

引入 file.txt 文件，修改 Makefile 文件。 file.txt  文件如下

```makefile
test:a.h
	@echo "this is a.h"
```

Makefile 文件如下

```makefile
all:test

include file.txt
test:b.h
	@echo "this is b.h"
test:c.h
	@echo "this is c.h"
	touch test
```

执行 make ，输出 类似与 例二。这里想说明的是， include 指令成功地将 file.txt 中的指令包含到 Makefile文件中了。

### 4.3.3 例三

下面我们继续对Makefile文件和file.txt文件进行修改，并新建d.h文件；

修改后的 file.txt 文件和Makefile文件的内容分别如下：

```makefile
file.txt:a.h
```

Makefile 文件

```makefile
all:test
include file.txt
file.txt:b.h
	@echo "test: d.h" > file.txt 
test:c.h
	@echo "this is c.h"
	touch test
```

我们依次输入以下命令：

```shell
$ls
a.h  b.h  c.h  d.h  file.txt  Makefile  test
$touch b.h
$touch d.h
$make
this is c.h
touch test
```

查看 file.txt 内容，发现内容变为

```makefile
test.txt:d.h
```

分析原因，include 命令包含了`file.txt`文件，故 include 命令会检查以`file.txt`为目标的全部规则。由于 `b.h` 比`file.txt`新，导致 `file.txt:b.h` 这条规则起作用，使得`file.txt`内容被更新。include 会将更新了的`file.txt`文件重新加到`Makefile`文件中，最终包含到 `Makefile`中的内容是`test.txt:d.h`，include 命令执行完毕。接着执行 all 目标的规则，由于更新了`d.h`，所以 test 目标要被更新，根据自下而上的执行规则，

```makefile
	test:c.h
	@echo "this is c.h"
	touch test
```

这条规则要被执行，输出如上。

### 4.3.4 例四

