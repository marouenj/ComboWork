Vagrant.configure("2") do |cluster|

    cluster.vm.define "combowork" do |machine|
        machine.vm.box = "ubuntu/trusty64"
        machine.vm.hostname = "combowork"

        machine.vm.provision "docker", images: ["golang:1.6", "java:8-jre", "marouenj/autopep8:latest", "marouenj/maven:latest", "python:3.5"]

        machine.vm.provider "virtualbox" do |vbox|
            vbox.name = "combowork"
            vbox.memory = 1024
        end
    end

end
