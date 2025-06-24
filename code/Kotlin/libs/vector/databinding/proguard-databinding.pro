#不混淆databinding自动生成的inflate的其中一个方法(被DBItemBinder调用)
-keepclassmembers class **Binding {
    public static inflate(android.view.LayoutInflater, android.view.ViewGroup, boolean);
}