var dgram = require('dgram');

var socket = dgram.createSocket('udp4');

socket.bind(1338);

socket.on('message', function (data, rinfo) {
  console.log('From '+ rinfo.address +':'+ rinfo.port);
  console.log(data.toString('utf8'));
});

var msg = "JOIN 1338\n\n";
socket.send(new Buffer(msg), 0, msg.length, 1337, "localhost");
