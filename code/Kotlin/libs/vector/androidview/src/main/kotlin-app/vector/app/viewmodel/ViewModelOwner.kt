package vector.app.viewmodel

internal interface ViewModelOwner<VM : ViewModelEx> {
    val viewModel: VM
}