# To run
```bash
# specify base dir
BASE_DIR=

docker run \
       --rm \
       --name combine \
       --net none \
       -v ${BASE_DIR}:/files \
combowork/combine:latest 
```
