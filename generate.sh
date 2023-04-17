cp $1 data
echo "Original file size: $(wc -c < $1) bytes"

#generate 2^$2times file $1
for ((n=0;n<$2; n++))
do
cat data data > /tmp/temp
mv /tmp/temp data
done

echo "Final file size: $(wc -c < data) bytes"
