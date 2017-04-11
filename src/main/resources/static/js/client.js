/*
 * EmergentMUD - A modern MUD with a procedurally generated world.
 * Copyright (C) 2016 Peter Keeler
 *
 * This file is part of EmergentMUD.
 *
 * EmergentMUD is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * EmergentMUD is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
var connected = false;
var socket = null;
var stompClient = null;
var commandHistory = [];
var commandHistoryIndex = -1;
var commandHistoryLength = 500;
var scrollBackLength = 5000;

$(document).ready(function() {
    $("#user-input-form").submit(function(event) {
        sendInput();
        event.preventDefault();
        return false;
    });

    connect();
});

$(document).keyup(function(event) {
    if (event.which === 38) { // up arrow
        commandHistoryIndex++;

        if (commandHistoryIndex >= commandHistory.length) {
            commandHistoryIndex = commandHistory.length - 1;
        }

        if (commandHistoryIndex >= 0) {
            $("#user-input").val(commandHistory[commandHistoryIndex]);
        }
    } else if (event.which === 40) { // down arrow
        commandHistoryIndex--;

        if (commandHistoryIndex < 0) {
            commandHistoryIndex = -1;
        }

        if (commandHistoryIndex >= 0) {
            $("#user-input").val(commandHistory[commandHistoryIndex]);
        } else {
            $("#user-input").val("");
        }
    }
});

function setConnected(newConnected) {
    connected = newConnected;
}

function connect() {
    socket = new SockJS('/mud');
    stompClient = Stomp.over(socket);
    stompClient.connect({},
    function(frame) {
        console.log('Connected: ' + frame);
        showOutput(["[green]Connected to server."]);

        stompClient.subscribe('/user/queue/output', function(message) {
            var msg = JSON.parse(message.body);
            showOutput(msg.output);
        },
        { "breadcrumb": breadcrumb });

        setConnected(true);
    },
    function() {
        setConnected(false);
        console.log('Disconnected.');
        showOutput(["[red]Disconnected from server."]);
    });
}

function sendInput() {
    var inputBox = $("#user-input");

    commandHistoryIndex = -1;
    commandHistory.unshift(inputBox.val());

    if (commandHistory.length > commandHistoryLength) {
        commandHistory.pop();
    }

    $("#output-list").find("li:last-child").append("<span class='yellow'> " + htmlEscape(inputBox.val()) + "</span>");

    stompClient.send("/app/input", { "breadcrumb": breadcrumb }, JSON.stringify({ 'input': inputBox.val() }));
    inputBox.val('');
}

function showOutput(message) {
    var outputBox = $("#output-box");
    var outputList = $("#output-list");

    for (var i = 0; i < message.length; i++) {
        if ("" === message[i]) {
            outputList.append("<li>&nbsp;</li>");
        } else {
            outputList.append("<li>" + replaceColors(message[i]) + "</li>");
        }
    }

    outputBox.prop("scrollTop", outputBox.prop("scrollHeight"));

    var scrollBackOverflow = outputList.find("li").length - scrollBackLength;

    if (scrollBackOverflow > 0) {
        outputList.find("li").slice(0, scrollBackOverflow).remove();
    }
}

function replaceColors(message) {
    return String(message)
        .replace(/\[default]/g, "<span class='default'>")
        .replace(/\[dblack]/g, "<span class='dblack'>")
        .replace(/\[black]/g, "<span class='black'>")
        .replace(/\[dwhite]/g, "<span class='dwhite'>")
        .replace(/\[white]/g, "<span class='white'>")
        .replace(/\[dred]/g, "<span class='dred'>")
        .replace(/\[red]/g, "<span class='red'>")
        .replace(/\[dyellow]/g, "<span class='dyellow'>")
        .replace(/\[yellow]/g, "<span class='yellow'>")
        .replace(/\[dgreen]/g, "<span class='dgreen'>")
        .replace(/\[green]/g, "<span class='green'>")
        .replace(/\[dcyan]/g, "<span class='dcyan'>")
        .replace(/\[cyan]/g, "<span class='cyan'>")
        .replace(/\[dblue]/g, "<span class='dblue'>")
        .replace(/\[blue]/g, "<span class='blue'>")
        .replace(/\[dmagenta]/g, "<span class='dmagenta'>")
        .replace(/\[magenta]/g, "<span class='magenta'>");
}

function htmlEscape(str) {
    return String(str)
        .replace(/&/g, '&amp;')
        .replace(/"/g, '&quot;')
        .replace(/'/g, '&#39;')
        .replace(/</g, '&lt;')
        .replace(/>/g, '&gt;')
        .replace(/\//g, '&#x2F;');
}