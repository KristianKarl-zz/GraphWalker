

var selectedButton;

function select(button) {
    if (undefined != selectedButton) {
        selectedButton.classList.remove("selected");
    }
    button.className += ' selected';
    selectedButton = button;
}