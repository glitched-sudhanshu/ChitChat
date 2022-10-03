const express = require('express'); //requires express module for http connection

//ye
const bodyParser = require('body-parser');
const socket = require('socket.io'); //requires socket.io module

const fs = require('fs');
const { SocketAddress } = require('net');
const app = express();
//ye
app.use(bodyParser.urlencoded({ extended: true }));
//ye
app.use(bodyParser.json());
var PORT = process.env.PORT || 3000; //server will run on port 3000
const server = app.listen(PORT); //tells to host server on localhost:3000


//Playing variables:
app.use(express.static('public')); //show static files in 'public' directory
console.log('Server is running');
const io = socket(server);

var count = 0;


//Socket.io Connection------------------
io.on('connection', (socket) => {

    console.log("New socket connection: " + socket.id)

    // socket.on('counter', () => {
    //     count++;
    //     console.log(count)
    //     io.emit('counter-re', count);
    // })

    // var Message = {
    //     userName: String,
    //     messageContent: String,
    //     roomName: String,
    //     senderType: Number
    // };

    socket.on('send-message-to-server', (messageContent, senderType, roomName)=>{
        
        // console.log("send-message-to-server: " + {messageContent} + " :: " + {senderType})
        if(roomName === ''){
            socket.broadcast.emit('send-message-to-receiver', messageContent, senderType)
        }else{
            socket.to(roomName).emit('send-message-to-receiver', messageContent, senderType)
        }
    })

    socket.on('join-room', roomName=>{
        socket.join(roomName)
        console.log("joined room " + socket.id + " : " + roomName)
    })

    socket.on('typing', roomName=>{
        socket.to(roomName).emit('typing-stat', true)
    })
})