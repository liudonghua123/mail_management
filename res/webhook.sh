#!/usr/bin/env bash

echo lastUpdateTime: `date "+%Y-%m-%d %H:%M:%S"` > lastUpdateTime.txt

app_name=mail_management
repo_path=/root/app/$app_name
app_version=2.2.1

update_code() {
  cd $repo_path
  git checkout . | tee -a /root/app/webhook.log
  git pull | tee -a /root/app/webhook.log
  cd -
}

build_frontend() {
  cd $repo_path/ant-design-vue-jeecg
  # yarn
  yarn build | tee -a /root/app/webhook.log
  cd -
}

build_backend() {
  cd $repo_path/jeecg-boot/jeecg-boot-module-app
  mvn clean package | tee -a /root/app/webhook.log
  cd -
}

fix_permission() {
  chmod a+x $repo_path/jeecg-boot/jeecg-boot-module-app/target/jeecg-boot-module-app-$app_version.jar
  chown -R nginx:nginx /root/app/dist
  chown -R nginx:nginx /root/app/dist/
}

post_run() {
  \cp -f /root/app/application-dev.yml /root/app/$app_name/jeecg-boot/jeecg-boot-module-app/target/
}

restart_server() {
  service colorful-server restart
}

echo 'update_code ...' | tee -a /root/app/webhook.log
update_code
echo 'build_frontend ...' | tee -a /root/app/webhook.log
build_frontend
echo 'build_backend ...' | tee -a /root/app/webhook.log
build_backend
echo 'fix_permission ...' | tee -a /root/app/webhook.log
fix_permission
echo 'post_run ...' | tee -a /root/app/webhook.log
post_run
echo 'restart_server ...' | tee -a /root/app/webhook.log
restart_server
