<template>
  <div class="footer">
    <div class="links">
      <a href="https://mail.ynu.edu.cn/" target="_blank"><a-icon type="mail" theme="twoTone"/> 云大邮件系统</a>
      <a href="https://www.ynu.edu.cn/" target="_blank"><a-icon type="heart" theme="twoTone" two-tone-color="#eb2f96" /> 云南大学</a>
      <a href="http://www.itc.ynu.edu.cn/" target="_blank"><a-icon type="code" theme="twoTone"/> 云南大学信息技术中心</a>
    </div>
    <div class="copyright">
      Copyright
      <a-icon type="copyright"/>
      2020 <span>云南大学</span>
    </div>
  </div>
</template>

<script>
  export default {
    name: "LayoutFooter"
  }
</script>

<style lang="less" scoped>
  .footer {
    padding: 0 16px;
    margin: 48px 0 24px;
    text-align: center;

    .links {
      margin-bottom: 8px;

      a {
        color: rgba(0, 0, 0, .45);

        &:hover {
          color: rgba(0, 0, 0, .65);
        }

        &:not(:last-child) {
          margin-right: 40px;
        }
      }
    }
    .copyright {
      color: rgba(0, 0, 0, .45);
      font-size: 14px;
    }
  }
</style>