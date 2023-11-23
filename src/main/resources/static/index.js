// form handler
const form = document.getElementById("picsInputForm");
const fileInput = document.getElementById("fileInput");

form.addEventListener("submit", uploadFiles);

async function uploadFiles(event) {
  event.preventDefault();
  const formData = new FormData();
  const files = fileInput.files;
  for (let i = 0; i < files.length; i++) {
    formData.append("file", files[i]);
  }
  const response = await fetch("/upload", {
    method: "POST",
    body: formData,
  });

  console.log({ response });
}
