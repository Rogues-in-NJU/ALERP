下午把公司支出写了，待单测，待post自测，待联调。在feature_lhl分支中。

问题列表：

- [x] 杨关建的dao，数据库里的datetime，应用层用String是不是比Date好一点？

  统一用string

- [x] 大法Entity的字段命名风格不统一，created_at和createdAt

  数据库里统一用created_at,使用@column，应用层用createdAt

- [ ] 增删改查要加异常处理，譬如增加一条数据库中id已有的记录？异常类？

- [ ] log日志都没打（和上文，异常处理结合）

- [x] 新增公司支出应该带上创建者id，删除公司支出应该带上删除者id？（接口有问题，或者我们去session里拿）

- [x] 查看公司列表接口，前端应该带上pageIndex和pageSize

  已确认，前端确实是要带上pageIndex和pageSize

- [x] 包名改成小写

- [x] 数据库表名统一小写

- [x] 都需要补一下单元测试

  重点逻辑补一下。

- [x] 都需要补一下注释。尤其是Entity的，每一个字段都要有注释；接口方法必须有注释

- [x] 互相review代码

- [ ] 同时修改一张表的处理

- [ ] 发号器？

- [x] 添加收款记录接口：前端传来的salesman应该是id不应该是string

  传的就是name不是id

- [ ] 错误：timeUtil里时间格式转换，精确到分而不是秒

- [x] 新增运行时异常类NJUException（之后可以做成环切）

  已完成。

- [ ] DAO层加不加全参/无参构造器

- [ ] 很多前台传姓名的接口，用户重名怎么办

- [x] 在userService里加接口：getIdFromName

  已完成
  
- [ ] session抽到工具类里，另：session还是cookie？
