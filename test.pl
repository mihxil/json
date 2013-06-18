while(<>) {
		chomp;
		my @items = split / /;
		my %hash = ();
		foreach(@items) {
				my @record = split /=/;
				$hash{$record[0]} = $record[1];
		}
		if ($hash{'type'} eq 'BROADCAST') {
				my $sortDate = $hash{'sortDate'};
				if (exists $hash{"start"}) {
						if ($hash{"start"} != $sortDate) {
								print "@items\n";
						}
				} else {
						if ($hash{"creationDate"} != $sortDate) {
								print "@items\n";
            }
				}
		}
}
