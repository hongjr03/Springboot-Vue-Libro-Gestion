<template>
  <!--  窗体代码  -->
  <div class="register">
    <el-container>
      <el-main>
        <!--  注册表单窗体  -->
        <el-row class="register-form">
          <!--  注册表单左面板 -->
          <el-col :span="12" class="register-form-left">
            <p>图书管理系统</p>
            <p>注册页面</p>
          </el-col>
          <!--  注册表单右面板 -->
          <el-col :span="12" class="register-form-right">
            <!--  注册表单  -->
            <el-form
              ref="registerFormRef"
              :model="registerForm"
              :rules="registerRules"
              @keyup.enter="register(registerFormRef)"
            >
              <el-form-item>
                <p>用户注册</p>
              </el-form-item>
              <el-form-item prop="username">
                <el-input
                  v-model="registerForm.username"
                  placeholder="用户名"
                ></el-input>
              </el-form-item>
              <el-form-item prop="password">
                <el-input
                  type="password"
                  v-model="registerForm.password"
                  placeholder="密码"
                ></el-input>
              </el-form-item>
              <el-form-item prop="confirmPassword">
                <el-input
                  type="password"
                  v-model="registerForm.confirmPassword"
                  placeholder="确认密码"
                ></el-input>
              </el-form-item>
              <el-form-item prop="email">
                <el-input
                  v-model="registerForm.email"
                  placeholder="邮箱"
                ></el-input>
              </el-form-item>
              <el-form-item prop="sendVerificationCode">
                <el-button
                  type="primary"
                  @click="sendVerificationCode"
                  size="small"
                >
                  发送验证码
                </el-button>
              </el-form-item>
              <el-form-item prop="verificationCode">
                <el-input
                  v-model="registerForm.code"
                  placeholder="验证码"
                ></el-input>
              </el-form-item>
              <el-form-item>
                <el-button
                  type="primary"
                  @click="register(registerFormRef)"
                  ref="registerButtonRef"
                  :disabled="registerState"
                  >注册</el-button
                >
              </el-form-item>
              <el-form-item>
                <el-button type="info" @click="login">返回登录</el-button>
              </el-form-item>
            </el-form>
          </el-col>
        </el-row>
      </el-main>
    </el-container>
  </div>
</template>

<script lang="ts" setup>
import { reactive, ref } from "vue";
import type { FormInstance, FormRules } from "element-plus";
import { ElMessageBox, ElButton } from "element-plus";

import axios from "axios";
import jsCookie from "js-cookie";
import router from "@/router";

// 注册表单
const registerButtonRef = ref();
let registerState = ref(false);

// 注册表单
const registerFormRef = ref<FormInstance>();
const registerForm = reactive({
  username: "",
  email: "",
  password: "",
  confirmPassword: "",
  code: "",
});

const login = () => {
  router.push("/login");
};

// 表单校验
const registerRules = reactive<FormRules>({
  username: [{ required: true, message: "请输入用户名", trigger: "blur" }],
  email: [
    { required: true, message: "请输入邮箱", trigger: "blur" },
    { type: "email", message: "请输入有效的邮箱地址", trigger: "blur" },
  ],
  password: [{ required: true, message: "请输入密码", trigger: "blur" }],
  confirmPassword: [
    { required: true, message: "请确认密码", trigger: "blur" },
    {
      validator: (rule, value, callback) => {
        if (value !== registerForm.password) {
          callback(new Error("两次输入的密码不一致"));
        } else {
          callback();
        }
      },
      trigger: "blur",
    },
  ],
});

// 发送验证码
const sendVerificationCode = () => {
  if (!registerForm.email) {
    ElMessageBox.alert("请输入邮箱", "信息", {
      confirmButtonText: "确认",
    });
    return;
  }
  axios
    .post("http://localhost:8888/register/sendVerificationCode", registerForm)
    .then((resp) => {
      const statusCode = resp.data.statusCode;

      // 发送验证码失败
      if (statusCode == 0) {
        ElMessageBox.alert("发送验证码失败", "信息", {
          confirmButtonText: "重试",
        });
      }

      // 发送验证码成功
      if (statusCode == 1) {
        ElMessageBox.alert("发送验证码成功", "信息", {
          confirmButtonText: "确认",
        });
      }
    });
};

// 注册操作
const register = (formEl: FormInstance | undefined) => {
  if (!formEl) return;
  formEl.validate((valid) => {
    if (valid && !registerState.value) {
      registerState.value = true;
      // 发送客户端请求
      axios
        .post("http://localhost:8888/register/user", registerForm)
        .then((resp) => {
          const statusCode = resp.data.statusCode;

          // 注册失败
          if (statusCode == 0) {
            ElMessageBox.alert("注册失败，用户名已存在", "信息", {
              confirmButtonText: "重试",
            });
            registerState.value = false;
          }
          if (statusCode == 1) {
            ElMessageBox.alert("注册失败，验证码无效", "信息", {
              confirmButtonText: "重试",
            });
            registerState.value = false;
          }
          // 注册成功
          if (statusCode == 2) {
            ElMessageBox.alert("注册成功", "信息", {
              confirmButtonText: "确认",
              callback: () => {
                // 设置Cookie
                jsCookie.set("username", resp.data.username, {
                  expires: 1,
                  path: "/",
                });
                // 页面跳转
                router.push("/home");
              },
            });
          }
        });
    } else {
      return false;
    }
  });
};
</script>

<style lang="scss">
@import "../assets/css/register";
</style>
