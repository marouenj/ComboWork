Vagrant.configure("2") do |cluster|

    cluster.vm.define "combowork" do |machine|
        machine.vm.box = "ubuntu/trusty64"
        machine.vm.hostname = "combowork"

        machine.vm.provision "docker", images: ["alpine:3.3"]
        machine.vm.provision "docker", images: ["golang:1.6"]
        machine.vm.provision "docker", images: ["java:8-jre", "maven:3-jdk-8"]
        machine.vm.provision "docker", images: ["python:3.5", "marouenj/autopep8:latest"]

        machine.vm.provider "virtualbox" do |vbox|
            vbox.name = "combowork"
            vbox.memory = 1024
        end
    end

end
