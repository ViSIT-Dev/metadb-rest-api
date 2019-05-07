//This code implements a paste function so that content can be pasted from an excel document to the page directly. 
//Even hidden structures (Wisski entries) are opened for that purpose.
//In this stage it is not possible to have multiple entries for a single element. Those still have to be managed manually.

//creates global elements that need to be updated from different functions
var input = document.createElement("input");
var wisskiAdditions = false;

//creates a given mouse click
function triggerMouseEvent(node, eventType) {
    var clickEvent = document.createEvent('MouseEvents');
    clickEvent.initEvent(eventType, true, true);
    node.dispatchEvent(clickEvent);
}

//removes the structuring lines that are used within the excel (for orientation) from the paste
function removeSeparationLines(parts) {
    var pageName = document.title;
    if (pageName.includes("Activity")) {
        parts.splice(13, 1);
        parts.splice(2, 1);
    } else if (pageName.includes("Architecture")) {
        parts.splice(49, 1);
        parts.splice(27, 1);
        parts.splice(25, 1);
        parts.splice(14, 1);
        parts.splice(13, 1);
    } else if (pageName.includes("Group")) {
        parts.splice(12, 1);
    } else if (pageName.includes("Reference")) {
        parts.splice(17, 1);
        parts.splice(13, 1);
        parts.splice(4, 1);
        parts.splice(0, 1);
    } else if (pageName.includes("Object")) {
        parts.splice(54, 1);
        parts.splice(36, 1);
        parts.splice(35, 1);
        parts.splice(25, 1);
        parts.splice(22, 1);
        parts.splice(14, 1);
        parts.splice(11, 1);
        parts.splice(3, 1);
    } else if (pageName.includes("Person")) {
        parts.splice(49, 1);
        parts.splice(31, 1);
        parts.splice(26, 1);
        parts.splice(24, 1);
        parts.splice(15, 1);
        parts.splice(14, 1);
        parts.splice(7, 1);
        parts.splice(5, 1);
    } else if (pageName.includes("Place")) {
        parts.splice(8, 1);
    }

    return parts;
}


//opens the first Wisski Addition including its sub-addition (if there)
function openFirstDimension(_callback) {
    //opens all "Add new wisski entry" fields so that all elements will be found later (first dimension)
    var wisskiAdds = [];

    wisskiAdds = document.querySelectorAll("input[id*='ief-add']");
    if (wisskiAdds.length > 0) {
        wisskiAdditions = true;
        var wisskiId = wisskiAdds[0].id;
        var openedFields = 0;
        //click on all buttons to open the fields
        if (!wisskiId.includes("save") && !wisskiId.includes("cancel")) {
            var targetNode = document.getElementById(wisskiId);
            if (targetNode) {
                triggerMouseEvent(targetNode, "mouseover");
                triggerMouseEvent(targetNode, "mousedown");
                triggerMouseEvent(targetNode, "mouseup");
                triggerMouseEvent(targetNode, "click");
                triggerMouseEvent(targetNode, "mousemove");
                ++openedFields;

            }
        }
    }

    var wisskiButtons = [];
    //construct an interval to check on the opening process
    var interval = setInterval(function () {
        if (wisskiButtons.length == openedFields) {
            var newWisskiAdds = document.querySelectorAll("input[id*='ief-add']");
            if (newWisskiAdds.length > 0) {
                var counter = 0;
                var wisskiId = newWisskiAdds[0].id;

                //if the id does not fit for a add-process, go through all ids and look if there is a suitable one
                //The save and cancel button include save oder cancel in their id and are not relevant here
                if (wisskiId.includes("save") || wisskiId.includes("cancel")) {
                    for (i = 0; i < newWisskiAdds.length; i++) {
                        wisskiId = newWisskiAdds[i].id;
                        if (!wisskiId.includes("save") && !wisskiId.includes("cancel")) {
                            i = newWisskiAdds.length;
                        }

                    }
                }

                if (!wisskiId.includes("save") && !wisskiId.includes("cancel")) {
                    //click on all buttons to open the fields
                    var targetNode = document.getElementById(wisskiId);
                    if (targetNode) {
                        targetNode.disabled = false;
                        triggerMouseEvent(targetNode, "mouseover");
                        triggerMouseEvent(targetNode, "mousedown");
                        triggerMouseEvent(targetNode, "mouseup");
                        triggerMouseEvent(targetNode, "click");
                        triggerMouseEvent(targetNode, "mousemove");
                        ++openedFields;

                    }
                }

            }

            //we need a second interval to check on the opening process of the sub-addition fields
            var secondInterval = setInterval(function () {
                if (wisskiButtons.length == openedFields) {
                    //when we have found everything return and call the next function
                    clearInterval(secondInterval);
                    clearInterval(interval);
                    _callback();
                } else {
                    wisskiButtons = document.querySelectorAll("input[id*='ief-add-save']");
                }
            }, 700);

        } else {
            //the number of save buttons show how many wisski additions are open (yet)
            wisskiButtons = document.querySelectorAll("input[id*='ief-add-save']");
        }
    }, 700);
}

function openSecondDimension(_callback) {
    //check how many buttons there are already
    var wisskiButtons = [];
    wisskiButtons = document.querySelectorAll("input[id*='ief-add-save']");
    var numberOfButtons = wisskiButtons.length;

    //find all remaining wisski addition fields
    var wisskiAdds = [];
    wisskiAdds = document.querySelectorAll("input[id*='ief-add']");
    if (wisskiAdds.length > 0) {
        wisskiAdditions = true;
        var openedFields = 0;

        var counter = 0;
        var wisskiId = wisskiAdds[0].id;

                        //if the first id does not fit for a add-process, go through all ids and look if there is a suitable one
                //The save and cancel button include save oder cancel in their id and are not relevant here
        if (wisskiId.includes("save") || wisskiId.includes("cancel")) {
            for (i = 0; i < wisskiAdds.length; i++) {
                wisskiId = wisskiAdds[i].id;
                if (!wisskiId.includes("save") && !wisskiId.includes("cancel")) {
                    i = wisskiAdds.length;
                }

            }
        }

        if (!wisskiId.includes("save") && !wisskiId.includes("cancel")) {
            //click on all buttons to open the fields
            var targetNode = document.getElementById(wisskiId);
            if (targetNode) {
                triggerMouseEvent(targetNode, "mouseover");
                triggerMouseEvent(targetNode, "mousedown");
                triggerMouseEvent(targetNode, "mouseup");
                triggerMouseEvent(targetNode, "click");
                triggerMouseEvent(targetNode, "mousemove");
                ++openedFields;
            }
        }
    }

    //construct an interval to check on the opening process
    var interval = setInterval(function () {
        if ((wisskiButtons.length - numberOfButtons) == openedFields) {
            var newWisskiAdds = document.querySelectorAll("input[id*='ief-add']");
            if (newWisskiAdds.length > 0) {
                var wisskiId = newWisskiAdds[0].id;
                if (wisskiId.includes("save") || wisskiId.includes("cancel")) {
                    for (i = 0; i < newWisskiAdds.length; i++) {
                        wisskiId = newWisskiAdds[i].id;
                        if (!wisskiId.includes("save") && !wisskiId.includes("cancel")) {
                            i = newWisskiAdds.length;
                        }

                    }
                }

                if (!wisskiId.includes("save") && !wisskiId.includes("cancel")) {
                    //click on all buttons to open the fields
                    var targetNode = document.getElementById(wisskiId);
                    if (targetNode) {
                        targetNode.disabled = false;
                        triggerMouseEvent(targetNode, "mouseover");
                        triggerMouseEvent(targetNode, "mousedown");
                        triggerMouseEvent(targetNode, "mouseup");
                        triggerMouseEvent(targetNode, "click");
                        triggerMouseEvent(targetNode, "mousemove");
                        ++openedFields;

                    }
                }
            }

            //we need a second interval to check on the opening process of the sub-addition fields
            var secondInterval = setInterval(function () {
                if ((wisskiButtons.length - numberOfButtons) == openedFields) {
                    clearInterval(secondInterval);
                    clearInterval(interval);
                    _callback();
                } else {
                    wisskiButtons = document.querySelectorAll("input[id*='ief-add-save']");
                }
            }, 700);

        } else {
            wisskiButtons = document.querySelectorAll("input[id*='ief-add-save']");
        }
    }, 700);
}


//put the paste information in the fields
//as only the information and not the line category is given, the values are put in one after another (without checking categories, etc.)
function fillIn(parts, _callback) {
    //find all possible elements after adding fields with ids that start with edit and are either input, select or textarea fields after all additional fields should be open
    var nodeElements = document.querySelectorAll("input[id^='edit'], textarea[id^='edit'], select[id^='edit']");
    var elements = Array.from(nodeElements);
    for (i = 0; i < elements.length; i++) {
        id = elements[i].id;
        //kick out select elements that are not important for the paste action as they are not input fields
        if (id.includes("weight") || id.includes("add-more") || id.includes("submit") || id.includes("save") || id.includes("cancel")) {
            elements.splice(i, 1);
            i = i - 1;
        }
    }



    //a time out is needed so that the paste from the user is overwritten by the separated and sorted inputs
    setTimeout(function () {
        //iterate over all paste parts and put them in the right field
        for (i = 0; i < parts.length; i++) {
            //if the element exists -> avoid null pointer exceptions
            if (elements[i]) {
                //get the element from the document
                var formElement = document.getElementById(elements[i].id);
                //if the element is a SELECT element, check for all options and select the right one
                if (formElement.nodeName === "SELECT") {
                    for (var o = 0; o < formElement.options.length; o++) {
                        var option = formElement.options[o];
                        if (option.text === parts[i]) {
                            formElement.options[0].selected = false;
                            option.selected = true;
                        }
                    }
                    //if the element is a date, bring the date into the right format
                } else if (formElement.id.includes("date")) {
                    var dateArray = parts[i].split(".");
                    formElement.value = dateArray[2] + "-" + dateArray[1] + "-" + dateArray[0];
                } else {
                    formElement.value = parts[i];
                }
            }
        }


        //give the user feedback about the handled paste action
        if (input) {
            if (wisskiAdditions) {
                input.style.color = "red";
                input.size = 95;
                input.value = "Bitte überprüfen Sie vor dem Speichern, dass Sie alle Wisski Entities auch über 'Create Wisski Entity' angelegt haben."
            } else {
                input.style.color = "green";
                input.size = 65;
                input.value = "Das Einfügen wurde durchgeführt. Bitte überprüfen Sie die Ergebnisse."
            }
        }

        _callback();

    }, 0);

}

function callPasteProcedures(event) {
    //give the user a message about the process
    var form = document.querySelectorAll("form[id$='form']");
    if (form) {
        //there is only one form within the document
        var formId = form.item(0).id;
        var div = document.createElement("div");
        div.id = "additionalWisskiDiv";
        input.id = "additionalWisskiInput";
        input.style.color = "orange";
        input.size = 30;
        input.value = "Der Einfügeprozess läuft.";
        input.readOnly;
        document.getElementById(formId).insertBefore(div, form.item(0).firstChild);
        document.getElementById(div.id).appendChild(input);
    }

    //gets content that is pasted by the user
    var clipText = event.clipboardData.getData('Text');
    //splits the string into parts (using new lines as separator)
    var parts = clipText.split(/\r?\n/);
    // remove Excel separation lines
    parts = removeSeparationLines(parts);

    var wisskiButtons = [];
    wisskiButtons = document.querySelectorAll("input[id*='ief-add']");
    var buttonLength = wisskiButtons.length;

    //according to how many "add" buttons for the Wisski Additions we can find, do the procedure X times
    if (buttonLength == 0) {
        fillIn(parts, function () {});
    } else if (buttonLength == 1) {
        openFirstDimension(function () {
            fillIn(parts, function () {});
        });
    } else if (buttonLength == 2) {
        openFirstDimension(function () {
            openSecondDimension(function () {
                fillIn(parts, function () {});
            });
        });
    } else if (buttonLength == 3) {
        openFirstDimension(function () {
            openSecondDimension(function () {
                openSecondDimension(function () {
                    fillIn(parts, function () {});
                });
            });
        });
    } else if (buttonLength == 4) {
        openFirstDimension(function () {
            openSecondDimension(function () {
                openSecondDimension(function () {
                    openSecondDimension(function () {
                        fillIn(parts, function () {});
                    });
                });
            });
        });
    } else if (buttonLength == 5) {
        openFirstDimension(function () {
            openSecondDimension(function () {
                openSecondDimension(function () {
                    openSecondDimension(function () {
                        openSecondDimension(function () {
                            fillIn(parts, function () {});
                        });
                    });
                });
            });
        });
    } else if (buttonLength == 6) {
        openFirstDimension(function () {
            openSecondDimension(function () {
                openSecondDimension(function () {
                    openSecondDimension(function () {
                        openSecondDimension(function () {
                            openSecondDimension(function () {
                                fillIn(parts, function () {});
                            });
                        });
                    });
                });
            });
        });
    } else {
        //if there are more than 6 wisski additions, either the user opened some fields or the layout got changed (and the paste wouldn't work anymore)
        alert("Bitte laden Sie die Seite erneut und versuchen es noch einmal, falls Sie vorher schon Felder geöffnet haben. Sollte es Änderungen am Layout gegeben haben, so kann das Paste-Feature nicht mehr zuverlässig funktionieren.")
    }



}

//adds an event listener to a paste event that can happen anywhere within the page
document.addEventListener('paste', function (event) {
    callPasteProcedures(event);
});
