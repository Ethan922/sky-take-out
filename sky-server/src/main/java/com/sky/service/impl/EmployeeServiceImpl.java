package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.PasswordConstant;
import com.sky.constant.StatusConstant;
import com.sky.context.BaseContext;
import com.sky.dto.EmployeeDTO;
import com.sky.dto.EmployeeLoginDTO;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.dto.PasswordEditDTO;
import com.sky.entity.Employee;
import com.sky.exception.*;
import com.sky.mapper.EmployeeMapper;
import com.sky.result.PageResult;
import com.sky.service.EmployeeService;
import io.swagger.models.auth.In;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    @Autowired
    private EmployeeMapper employeeMapper;

    /**
     * 员工登录
     *
     * @param employeeLoginDTO
     * @return
     */
    public Employee login(EmployeeLoginDTO employeeLoginDTO) {
        String username = employeeLoginDTO.getUsername();
        String password = employeeLoginDTO.getPassword();

        //1、根据用户名查询数据库中的数据
        Employee employee = employeeMapper.getByUsername(username);

        //2、处理各种异常情况（用户名不存在、密码不对、账号被锁定）
        if (employee == null) {
            //账号不存在
            throw new AccountNotFoundException(MessageConstant.ACCOUNT_NOT_FOUND);
        }

        //密码比对
        password = DigestUtils.md5DigestAsHex(password.getBytes());
        if (!password.equals(employee.getPassword())) {
            //密码错误
            throw new PasswordErrorException(MessageConstant.PASSWORD_ERROR);
        }

        if (employee.getStatus() == StatusConstant.DISABLE) {
            //账号被锁定
            throw new AccountLockedException(MessageConstant.ACCOUNT_LOCKED);
        }

        //3、返回实体对象
        return employee;
    }

    /**
     * 新增员工
     *
     * @param employeeDTO
     */
    @Override
    public void createEmp(EmployeeDTO employeeDTO) {
        Long empId = employeeMapper.selectEmpIdByUername(employeeDTO.getUsername());
        if (empId!=null){
            throw new EmployeeUsernameDuplicateException(MessageConstant.EMPLOYEE_USERNAME_DUPLICATE);
        }
        Employee employee = new Employee();
        //对象属性拷贝
        BeanUtils.copyProperties(employeeDTO, employee);

        //设置默认密码
        employee.setPassword(DigestUtils.md5DigestAsHex(PasswordConstant.DEFAULT_PASSWORD.getBytes()));

        //设置状态码
        employee.setStatus(StatusConstant.ENABLE);

        employeeMapper.insertEmp(employee);
    }

    @Override
    public PageResult page(EmployeePageQueryDTO employeePageQueryDTO) {
        PageHelper.startPage(employeePageQueryDTO.getPage(), employeePageQueryDTO.getPageSize());
        Page<Employee> page = employeeMapper.pageQuery(employeePageQueryDTO);
        return PageResult.builder()
                .total(page.getTotal())
                .records(page.getResult()).build();
    }

    /**
     * 禁用或者启用员工账号
     *
     * @param id
     * @param status
     */
    @Override
    public void changeStatus(Long id, Integer status) {
        Employee employee = Employee.builder()
                .status(status)
                .id(id)
                .build();
        employeeMapper.update(employee);
    }

    /**
     * 根据id查询员工
     *
     * @param id
     * @return
     */
    @Override
    public Employee getById(Long id) {
        Employee employee = employeeMapper.selectById(id);
        //将返回的密码设置为****，保证安全性
        employee.setPassword("*****");
        return employee;
    }

    /**
     * 编辑员工信息
     *
     * @param employeeDTO
     */
    @Override
    public void editEmp(EmployeeDTO employeeDTO) {
            Long empId = employeeMapper.selectEmpIdByUername(employeeDTO.getUsername());
            if (empId!=null&&empId!=employeeDTO.getId()){
                throw new EmployeeUsernameDuplicateException(MessageConstant.EMPLOYEE_USERNAME_DUPLICATE);
            }
            Employee employee = new Employee();
            BeanUtils.copyProperties(employeeDTO, employee);
            employeeMapper.update(employee);
    }

    /**
     * 修改密码
     *
     * @param passwordEditDTO
     */
    @Override
    public void editPasswod(PasswordEditDTO passwordEditDTO) {
        //将旧密码MD5加密
        String oldPassword = DigestUtils.md5DigestAsHex(passwordEditDTO.getOldPassword().getBytes());
        Employee employee = employeeMapper.selectById(BaseContext.getCurrentId());
        if (!employee.getPassword().equals(oldPassword)) {
            //旧密码错误
            throw new PasswordEditFailedException("原密码错误");
        } else {
            //将新密码MD5加密
            String newPassword = DigestUtils.md5DigestAsHex(passwordEditDTO.getNewPassword().getBytes());
            employeeMapper.update(Employee.builder().id(BaseContext.getCurrentId()).password(newPassword).build());
        }
    }

}
